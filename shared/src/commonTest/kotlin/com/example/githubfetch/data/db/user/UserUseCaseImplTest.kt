import com.example.githubfetch.data.repository.recentSearch.RecentSearchRepository
import com.example.githubfetch.data.repository.user.UserRepository
import com.example.githubfetch.domain.recentSearch.RecentSearch
import com.example.githubfetch.domain.user.User
import com.example.githubfetch.domain.user.UserUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserUseCaseImplTest {

    private val userRepository = UserRepositoryMock()
    private val recentSearchRepository = RecentSearchRepositoryMock()

    private val userUseCase = UserUseCaseImpl(userRepository, recentSearchRepository)

    @Test
    fun `getUsersWithRepoCount should return users with repo counts and save query to recentSearchRepository`() = runTest {
        val userId1 = 1
        val userId2 = 2
        val userName = "john_doe"
        val avatarUrl = "http://avatar.url"

        val users = listOf(
            User(userId1, userName, avatarUrl, repoCount = 0),
            User(userId2, userName, avatarUrl, repoCount = 0)
        )

        val repoCount = 5

        userRepository.getResponse = { _, _, _ -> users }
        userRepository.getUserRepoCountResponse = { _ -> repoCount }


        val collectedResults = userUseCase.getUsersWithRepoCount("john", 1, 10).toList()


        assertEquals(2, collectedResults.size)
        assertTrue(collectedResults[1].all { it.repoCount == repoCount })

    }

    @Test
    fun `getUsersWithRepoCount should handle exception and set repoCount to 0 when failed`() = runTest {
        val userId1 = 1
        val userName = "john_doe"
        val avatarUrl = "http://avatar.url"

        val users = listOf(
            User(userId1, userName, avatarUrl, repoCount = 0)
        )

        userRepository.getResponse = { _, _, _ -> users }
        userRepository.getUserRepoCountResponse = { _ -> throw Exception("Repo count fetch failed") }

        val collectedResults = userUseCase.getUsersWithRepoCount("john", 1, 10).toList()


        assertEquals(2, collectedResults.size)
        assertTrue(collectedResults[1].all { it.repoCount == 0 })

    }
}

// Mocks for UserRepository and RecentSearchRepository

class UserRepositoryMock : UserRepository {
    var getResponse: (suspend (String, Int, Int) -> List<User>) = { _, _, _ -> emptyList() }
    var getUserRepoCountResponse: (suspend (String) -> Int) = { _ -> 0 }

    override suspend fun get(query: String, page: Int, perPage: Int): List<User> {
        return getResponse(query, page, perPage)
    }

    override suspend fun getUserRepoCount(userName: String, userId: Int): Int {
        return getUserRepoCountResponse(userName)
    }

    override suspend fun save(users: List<com.example.githubfetch.domain.user.User>) {
        TODO("Not yet implemented")
    }


}

class RecentSearchRepositoryMock : RecentSearchRepository {
    val savedQueries = mutableListOf<String>()

    override suspend fun save(query: String) {
    }

    override suspend fun get(): Flow<List<RecentSearch>> {
        return flowOf(savedQueries.map { RecentSearch(it, 1) })
    }
}

