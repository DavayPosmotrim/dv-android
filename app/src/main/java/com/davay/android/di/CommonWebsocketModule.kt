package com.davay.android.di

import com.davay.android.core.data.dto.SessionResultDto
import com.davay.android.core.data.dto.SessionStatusDto
import com.davay.android.core.data.dto.UserDto
import com.davay.android.core.data.impl.SessionResultWebsocketRepositoryImpl
import com.davay.android.core.data.impl.SessionStatusWebsocketRepositoryImpl
import com.davay.android.core.data.impl.UsersWebsocketRepositoryImpl
import com.davay.android.core.data.network.WebsocketNetworkClient
import com.davay.android.core.data.network.WebsocketSessionResultClient
import com.davay.android.core.data.network.WebsocketSessionStatusClient
import com.davay.android.core.data.network.WebsocketUsersClient
import com.davay.android.core.domain.api.SessionResultWebsocketRepository
import com.davay.android.core.domain.api.SessionStatusWebsocketRepository
import com.davay.android.core.domain.api.UsersWebsocketRepository
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import dagger.Module
import dagger.Provides

@Module
class CommonWebsocketModule {

    @Provides
    fun provideWebsocketSessionStatusClient(): WebsocketNetworkClient<SessionStatusDto, String> {
        return WebsocketSessionStatusClient()
    }

    @Provides
    fun provideSessionStatusWebsocketRepository(
        client: WebsocketNetworkClient<SessionStatusDto, String>
    ): SessionStatusWebsocketRepository {
        return SessionStatusWebsocketRepositoryImpl(client)
    }

    @Provides
    fun provideWebsocketUsersClient(): WebsocketNetworkClient<List<UserDto>, String> {
        return WebsocketUsersClient()
    }

    @Provides
    fun provideUsersWebsocketRepository(
        client: WebsocketNetworkClient<List<UserDto>, String>
    ): UsersWebsocketRepository {
        return UsersWebsocketRepositoryImpl(client)
    }

    @Provides
    fun provideWebsocketSessionResultClient(): WebsocketNetworkClient<SessionResultDto?, String> {
        return WebsocketSessionResultClient()
    }

    @Provides
    fun provideSessionResultWebsocketRepository(
        client: WebsocketNetworkClient<SessionResultDto?, String>
    ): SessionResultWebsocketRepository {
        return SessionResultWebsocketRepositoryImpl(client)
    }

    @Provides
    fun provideCommonWebsocketInteractor(
        sessionStatusWebsocketRepository: SessionStatusWebsocketRepository,
        usersWebsocketRepository: UsersWebsocketRepository,
        sessionResultWebsocketRepository: SessionResultWebsocketRepository,
    ): CommonWebsocketInteractor {
        return CommonWebsocketInteractor(
            sessionStatusWebsocketRepository,
            usersWebsocketRepository,
            sessionResultWebsocketRepository,
        )
    }
}