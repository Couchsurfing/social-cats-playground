/*
 * Copyright 2012-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.amazonaws.http;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;

import static org.apache.http.protocol.HttpCoreContext.HTTP_TARGET_HOST;

/**
 * An {@link HttpRequestInterceptor} that signs requests using any AWS {@link Signer}
 * and {@link AwsCredentialsProvider}.
 */
public class AWSRequestSigningApacheInterceptor implements HttpRequestInterceptor {
    /**
     * The service that we're connecting to. Technically not necessary.
     * Could be used by a future Signer, though.
     */
    private final Aws4SignerParams signerParams;

    /**
     * The particular signer implementation.
     */
    private final Aws4Signer signer;


    /**
     * @param signerParams signerParams
     * @param signer       particular signer implementation
     */
    public AWSRequestSigningApacheInterceptor(final Aws4SignerParams signerParams,
                                              final Aws4Signer signer) {
        this.signerParams = signerParams;
        this.signer = signer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final HttpRequest request, final HttpContext context)
            throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(request.getRequestLine().getUri());
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI", e);
        }


        SdkHttpFullRequest.Builder signableRequest = SdkHttpFullRequest.builder();


        HttpHost host = (HttpHost) context.getAttribute(HTTP_TARGET_HOST);
        if (host != null) {
            signableRequest.uri(URI.create(host.toURI()));
        }
        final SdkHttpMethod httpMethod =
                SdkHttpMethod.fromValue(request.getRequestLine().getMethod());
        signableRequest.method(httpMethod);
        try {
            signableRequest.encodedPath(uriBuilder.build().getRawPath());
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI", e);
        }


        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest httpEntityEnclosingRequest =
                    (HttpEntityEnclosingRequest) request;
            if (httpEntityEnclosingRequest.getEntity() != null) {
                signableRequest.contentStreamProvider(() -> {
                    try {
                        return httpEntityEnclosingRequest.getEntity().getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
            }
        }
        nvpToMapParams(signableRequest, uriBuilder.getQueryParams());
        headerArrayToMap(signableRequest, request.getAllHeaders());

        // Sign it
        SdkHttpFullRequest signedRequest = signer.sign(signableRequest.build(), signerParams);

        // Now copy everything back
        request.setHeaders(mapToHeaderArray(signedRequest.headers()));
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest httpEntityEnclosingRequest =
                    (HttpEntityEnclosingRequest) request;
            if (httpEntityEnclosingRequest.getEntity() != null) {
                BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
                basicHttpEntity.setContent(signedRequest.contentStreamProvider().get().newStream());
                httpEntityEnclosingRequest.setEntity(basicHttpEntity);
            }
        }
    }

    /**
     * @param builder
     * @param params  list of HTTP query params as NameValuePairs
     * @return a multimap of HTTP query params
     */
    private static void nvpToMapParams(SdkHttpFullRequest.Builder builder, final List<NameValuePair> params) {
        for (NameValuePair nvp : params) {
            builder.putRawQueryParameter(nvp.getName(), nvp.getValue());
        }
    }

    /**
     * @param builder
     * @param headers modeled Header objects
     * @return a Map of header entries
     */
    private static void headerArrayToMap(SdkHttpFullRequest.Builder builder, final Header[] headers) {

        for (Header header : headers) {
            if (!skipHeader(header)) {
                builder.putHeader(header.getName(), header.getValue());
            }
        }
    }

    /**
     * @param header header line to check
     * @return true if the given header should be excluded when signing
     */
    private static boolean skipHeader(final Header header) {
        return ("content-length".equalsIgnoreCase(header.getName())
                && "0".equals(header.getValue())) // Strip Content-Length: 0
                || "host".equalsIgnoreCase(header.getName()); // Host comes from endpoint
    }

    /**
     * @param mapHeaders Map of header entries
     * @return modeled Header objects
     */
    private static Header[] mapToHeaderArray(final Map<String, List<String>> mapHeaders) {
        Header[] headers = new Header[mapHeaders.size()];
        int i = 0;
        for (Map.Entry<String, List<String>> headerEntry : mapHeaders.entrySet()) {
            headers[i++] = new BasicHeader(headerEntry.getKey(), headerEntry.getValue().get(0));
        }
        return headers;
    }
}