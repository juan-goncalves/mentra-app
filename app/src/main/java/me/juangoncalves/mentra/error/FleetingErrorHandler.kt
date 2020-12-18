package me.juangoncalves.mentra.error


//fun Fragment.showSnackbarWhen(
//    publisher: FleetingFailurePublisher
//) {
//    publisher.fleetingErrorStream.observe(viewLifecycleOwner) { event ->
//        event.use { fleetingError ->
//            Snackbar.make(requireView(), fleetingError.message, Snackbar.LENGTH_LONG)
//                .applyErrorStyle()
//                .apply {
//                    fleetingError.retryAction?.let {
//                        setAction(R.string.retry) {
//                            fleetingError.retryAction
//                        }
//                    }
//                }
//                .show()
//        }
//    }
//}

