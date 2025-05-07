package de.example.met_gallery.data

import de.example.met_gallery.network.ArtworkApi
import de.example.met_gallery.network.ArtworkDataSource
import de.example.met_gallery.network.ArtworkDataSourceImpl
import de.example.met_gallery.network.ArtworkRepository
import de.example.met_gallery.network.ArtworkRepositoryImpl
import de.example.met_gallery.network.SearchArtworksUseCase
import de.example.met_gallery.ui.screens.detail.DetailViewModel
import de.example.met_gallery.ui.screens.search.SearchViewModel
import retrofit2.Retrofit
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<AppContainer> { DefaultAppContainer() }

    viewModel {
        SearchViewModel (
            artworkRepository = get(),
            searchArtworks = SearchArtworksUseCase(get())
        )
    }

    viewModelOf(::DetailViewModel)

    single { get<Retrofit>().create(ArtworkApi::class.java) }

    singleOf(::ArtworkDataSourceImpl) bind ArtworkDataSource::class
    single<ArtworkRepository> {
        ArtworkRepositoryImpl(get<AppContainer>(). artworkDataSource)
    }
}

