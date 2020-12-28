/*
 * Copyright 2020 Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.shreyaspatil.noty.composeapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.shreyaspatil.noty.composeapp.datastore.UIPreference
import dev.shreyaspatil.noty.composeapp.datastore.UiMode
import dev.shreyaspatil.noty.composeapp.navigation.Main
import dev.shreyaspatil.noty.composeapp.ui.NotyTheme
import dev.shreyaspatil.noty.view.viewmodel.AddNoteViewModel
import dev.shreyaspatil.noty.view.viewmodel.LoginViewModel
import dev.shreyaspatil.noty.view.viewmodel.NotesViewModel
import dev.shreyaspatil.noty.view.viewmodel.RegisterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val prefsManager = UIPreference(context = this)
        lifecycleScope.launch {
            prefsManager.uiModeFlow.collect {
                when (it) {
                    UiMode.DARK -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    UiMode.LIGHT -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            val currentTheme = isSystemInDarkTheme()
            val darkMode by prefsManager.uiModeFlow.map { uiMode ->
                when (uiMode) {
                    UiMode.DARK -> {
                        true
                    }
                    UiMode.LIGHT -> {
                        false
                    }
                }
            }.collectAsState(initial = currentTheme)

            val toggleTheme: () -> Unit = {
                lifecycleScope.launch {
                    prefsManager.setUiMode(if (darkMode) UiMode.LIGHT else UiMode.DARK)
                }
            }

            NotyTheme(darkTheme = darkMode) {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val registerViewModel: RegisterViewModel = viewModel()
                    val loginViewModel: LoginViewModel = viewModel()
                    val addNoteViewModel: AddNoteViewModel = viewModel()
                    val notesViewModel: NotesViewModel = viewModel()
                    Main(
                        toggleTheme = toggleTheme,
                        registerViewModel,
                        loginViewModel,
                        notesViewModel,
                        addNoteViewModel
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotyTheme {
    }
}