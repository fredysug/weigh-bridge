/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.core.data.util

import android.util.Log

object Logger {
    private const val TAG = "weigh-bridge"

    fun v(tag: String, message: String) = Log.v(tag, message)
    fun v(message: String) = v(TAG, message)
    fun d(tag: String, message: String) = Log.d(tag, message)
    fun d(message: String) = d(TAG, message)
    fun i(tag: String, message: String) = Log.i(tag, message)
    fun i(message: String) = i(TAG, message)
    fun w(tag: String, message: String) = Log.w(tag, message)
    fun w(message: String) = w(TAG, message)
    fun e(tag: String, message: String) = Log.e(tag, message)
    fun e(message: String) = e(TAG, message)

}
