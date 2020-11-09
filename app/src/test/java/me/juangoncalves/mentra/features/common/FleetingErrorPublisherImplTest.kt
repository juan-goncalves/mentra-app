package me.juangoncalves.mentra.features.common

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import either.Either
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.Left
import me.juangoncalves.mentra.MainCoroutineRule
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.usecases.VoidUseCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FleetingErrorPublisherImplTest {

    @get:Rule val coroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @MockK lateinit var observer: Observer<Event<DisplayError>>

    private lateinit var sut: FleetingErrorPublisherImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = FleetingErrorPublisherImpl()
        sut.fleetingErrorStream.observeForever(observer)
    }

    @Test
    fun `emits a fleeting error when the use case fails`() = runBlockingTest {
        // Arrange
        val captor = slot<Event<DisplayError>>()

        // Act
        with(sut) {
            failingUseCaseFake.executor()
                .inScope(this@runBlockingTest)
                .withDispatcher(Dispatchers.Main)
                .onFailurePublishFleetingError()
                .run()
        }

        // Assert
        verify(exactly = 1) { observer.onChanged(capture(captor)) }
        assertNotNull(captor.captured.content)
    }

    @Test
    fun `does not emit an error when the use case successfully executes`() = runBlockingTest {
        // Act
        with(sut) {
            successfulUseCaseFake.executor()
                .inScope(this@runBlockingTest)
                .withDispatcher(Dispatchers.Main)
                .onFailurePublishFleetingError()
                .run()
        }

        // Assert
        verify(exactly = 0) { observer.onChanged(any()) }
    }

    private val failingUseCaseFake = object : VoidUseCase<String> {
        override suspend fun invoke(): Either<Failure, String> = Left(StorageFailure())
    }

    private val successfulUseCaseFake = object : VoidUseCase<String> {
        override suspend fun invoke(): Either<Failure, String> = Right("Success")
    }

}