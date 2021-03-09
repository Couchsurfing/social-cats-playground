package com.myorg

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.IOException
import software.amazon.awscdk.core.App
import com.myorg.AwsCdkStack
import com.myorg.AwsCdkStackTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class AwsCdkStackTest {
    @Test
    @Throws(IOException::class)
    fun testStack() {
        val app = App()
        val stack = AwsCdkStack(app, "test", null, false)
        val actual = JSON.valueToTree<JsonNode>(app.synth().getStackArtifact(stack.artifactId).template)
        Assertions.assertThat(actual.toString())
            .contains("AWS::SQS::Queue")
            .contains("AWS::SNS::Topic")
    }

    companion object {
        private val JSON = ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true)
    }
}