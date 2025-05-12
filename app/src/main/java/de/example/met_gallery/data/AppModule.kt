package de.example.met_gallery.data

import de.example.met_gallery.network.ArtworkApi
import de.example.met_gallery.network.ArtworkDataSource
import de.example.met_gallery.network.ArtworkDataSourceImpl
import de.example.met_gallery.network.ArtworkRepository
import de.example.met_gallery.network.ArtworkRepositoryImpl
import de.example.met_gallery.ui.screens.search.state.SearchViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    single<AppContainer> { DefaultAppContainer() }

    viewModel {
        SearchViewModel(
            artworkRepository = get()
        )
    }

    single { get<Retrofit>().create(ArtworkApi::class.java) }

    singleOf(::ArtworkDataSourceImpl) bind ArtworkDataSource::class
    single<ArtworkRepository> {
        ArtworkRepositoryImpl(get<AppContainer>().artworkDataSource)
    }
}
