/*
  * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.nicolasmilliard.services.lambda.runtime.events.model.dynamodb

public enum class OperationType(private val value: String) {
    INSERT("INSERT"), MODIFY("MODIFY"), REMOVE("REMOVE");

    override fun toString(): String {
        return value
    }

    public companion object {
        /**
         * Use this in place of valueOf.
         *
         * @param value
         * real value
         * @return OperationType corresponding to the value
         *
         * @throws IllegalArgumentException
         * If the specified value does not map to one of the known values in this enum.
         */
        public fun fromValue(value: String?): OperationType {
            require(!(value == null || "" == value)) { "Value cannot be null or empty!" }
            for (enumEntry in values()) {
                if (enumEntry.toString() == value) {
                    return enumEntry
                }
            }
            throw IllegalArgumentException("Cannot create enum from $value value!")
        }
    }
}