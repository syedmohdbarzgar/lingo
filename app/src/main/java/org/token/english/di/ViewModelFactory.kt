package org.token.english.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.token.english.EnglishApp

/**
 * Builds a ViewModel factory wired to the app's [AppContainer].
 * Usage: val vm: HomeViewModel = viewModel(factory = appViewModelFactory { HomeViewModel(it) })
 */
inline fun <reified VM : ViewModel> appViewModelFactory(
    crossinline create: (AppContainer) -> VM,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EnglishApp
        create(app.container)
    }
}
