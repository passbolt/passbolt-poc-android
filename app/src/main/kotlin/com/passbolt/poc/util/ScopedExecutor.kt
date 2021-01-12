package com.passbolt.poc.util

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

// taken from https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart
class ScopedExecutor constructor(private val executor: Executor) : Executor {
  private val shutdown: AtomicBoolean = AtomicBoolean()
  override fun execute(command: Runnable) {
    if (shutdown.get()) {
      return
    }
    executor.execute(
        Runnable {
          if (shutdown.get()) {
            return@Runnable
          }
          command.run()
        }
    )
  }

  fun shutdown() {
    shutdown.set(true)
  }
}
