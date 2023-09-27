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

package android.template.ui

import android.template.core.ui.Screens
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.template.feature.weighbridge.ui.add.AddTicketScreen
import android.template.feature.weighbridge.ui.edit.EditTicketScreen
import android.template.feature.weighbridge.ui.list.TicketListScreen
import android.util.Log

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val startNavigation = Screens.Home.route

    NavHost(navController = navController, startDestination = startNavigation) {
        composable(Screens.Home.route) {
            TicketListScreen(modifier = Modifier.padding(16.dp), navController = navController)
        }
        composable(Screens.AddTicket.route) {
            AddTicketScreen(modifier = Modifier.padding(16.dp), navController = navController)
        }
        composable(Screens.EditTicket.route) {
            val ticketUid = it.arguments?.getString("id")?.toInt() ?: 0
            EditTicketScreen(
                modifier = Modifier.padding(16.dp),
                ticketUid = ticketUid,
                navController = navController
            )
        }
        // TODO: Add more destinations0
    }
}
