package me.juangoncalves.mentra.domain_layer.errors

sealed class Failure {

    object Network : Failure()

    object NotFound : Failure()

    object ServiceUnavailable : Failure()

    object AccessDenied : Failure()

    object InvalidRequest : Failure()

    object Unknown : Failure()

}