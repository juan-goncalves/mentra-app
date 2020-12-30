package me.juangoncalves.mentra.domain_layer.usecases

import kotlinx.coroutines.flow.Flow


interface FlowUseCase<Result> {

    operator fun invoke(): Flow<Result>

}
