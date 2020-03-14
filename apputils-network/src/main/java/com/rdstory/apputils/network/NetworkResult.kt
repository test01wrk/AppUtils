package com.rdstory.apputils.network

import java.io.Serializable

public class NetworkResult<out T> @PublishedApi internal constructor(@PublishedApi internal val value: Any?) : Serializable {
    // discovery

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    public val isSuccess: Boolean get() = value !is NetworkFailure

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    public val isFailure: Boolean get() = value is NetworkFailure

    // value & exception retrieval

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or `null`
     * if it is [failure][Result.isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    public fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as? T
        }

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    public fun exceptionOrNull(): Throwable? =
        when (value) {
            is NetworkFailure -> value.exception
            else -> null
        }

    /**
     * Returns a string `Success(v)` if this instance represents [success][Result.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    public override fun toString(): String =
        when (value) {
            is NetworkFailure -> value.toString() // "Failure($exception)"
            else -> "Success($value)"
        }

    // companion with constructors

    /**
     * Companion object for [Result] class that contains its constructor functions
     * [success] and [failure].
     */
    public companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        public fun <T> success(value: T): NetworkResult<T> =
            NetworkResult(value)

        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         */
        public fun <T> failure(exception: Throwable): NetworkResult<T> =
            NetworkResult(NetworkFailure(exception))
    }

    @PublishedApi internal class NetworkFailure(@PublishedApi internal val exception: Throwable) : Serializable {
        override fun equals(other: Any?): Boolean = other is NetworkFailure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
}