package com.squareup.picasso

/**
 *  Clear the memory cache. This has to be in this package.
 */
fun Picasso.clearCache() {
    cache.clear()
}