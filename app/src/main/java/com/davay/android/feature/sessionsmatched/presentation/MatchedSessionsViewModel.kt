package com.davay.android.feature.sessionsmatched.presentation

import androidx.lifecycle.viewModelScope
import com.davay.android.base.BaseViewModel
import com.davay.android.domain.models.Session
import com.davay.android.domain.models.SessionStatus
import com.davay.android.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MatchedSessionsViewModel @Inject constructor() : BaseViewModel() {
    private val _state = MutableStateFlow<MatchedSessionsState>(MatchedSessionsState.Loading)
    val state = _state.asStateFlow()

    init {
        getMatchedSessions()
    }

    private fun getMatchedSessions() {
        viewModelScope.launch {
            _state.value = MatchedSessionsState.Content(matchedSessions)
        }
    }
}

@Suppress("Detekt.MagicNumber", "Detekt.StringLiteralDuplication")
private val matchedSessions = listOf(
    Session(
        id = "11Sq21",
        users = listOf(
            User(userId = "dqwdqw123dad", name = "Riley Bryan"),
            User(userId = "mandamus", name = "Adolfo Burks"),
            User(userId = "molestie", name = "Kate Griffin"),
            User(userId = "ponderum", name = "Earl England"),
            User(userId = "senserit", name = "Maribel Daniel")
        ),
        numberOfMatchedMovies = null,
        date = "2024-05-23",
        status = SessionStatus.VOTING,
        imgUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1704946/0a54b73c-bbec-4ec0-9516-0faaf7ebe9fa/3840x"
    ),
    Session(
        id = "223q21",
        users = listOf(
            User(userId = "dqwdqw123dad", name = "Riley Bryan"),
            User(userId = "convallis", name = "Liza Cotton"),
            User(userId = "mandamus", name = "Adolfo Burks"),
            User(userId = "consectetuer", name = "Lavonne Beasley")
        ),
        numberOfMatchedMovies = 6,
        date = "2024-06-03",
        status = SessionStatus.CLOSED,
        imgUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/4303601/119d3aad-a479-4532-aadf-b428eb330337/3840x"
    ),
    Session(
        id = "curae",
        users = listOf(
            User(userId = "dqwdqw123dad", name = "Riley Bryan"),
            User(userId = "mandamus", name = "Adolfo Burks"),
            User(userId = "senserit", name = "Maribel Daniel"),
            User(userId = "convallis", name = "Liza Cotton")
        ),
        numberOfMatchedMovies = 4,
        date = "2024-06-20",
        status = SessionStatus.WAITING,
        imgUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1600647/6e9e2641-5750-4991-a52b-8c1cd0e80b4c/3840x"
    ),
    Session(
        id = "risus",
        users = listOf(
            User(userId = "dqwdqw123dad", name = "Riley Bryan"),
            User(userId = "contentiones", name = "Forest Summers"),
            User(userId = "molestiae", name = "Sherri Saunders"),
            User(userId = "molestie", name = "Kate Griffin"),
            User(userId = "ponderum", name = "Earl England"),
            User(userId = "senserit", name = "Maribel Daniel")
        ),
        numberOfMatchedMovies = 3,
        date = "2024-06-21",
        status = SessionStatus.CLOSED,
        imgUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/4774061/0d60959d-fef2-4f1f-a28b-3d08247ec817/3840x"
    ),
    Session(
        id = "i1243hr36w",
        users = listOf(
            User(userId = "dqwdqw123dad", name = "Riley Bryan"),
            User(userId = "contentiones", name = "Forest Summers"),
            User(userId = "molestiae", name = "Sherri Saunders"),
            User(userId = "molestie", name = "Kate Griffin"),
            User(userId = "ponderum", name = "Earl England"),
            User(userId = "senserit", name = "Maribel Daniel")
        ),
        numberOfMatchedMovies = 3,
        date = "2024-06-27",
        status = SessionStatus.CLOSED,
        imgUrl = ""
    )
)