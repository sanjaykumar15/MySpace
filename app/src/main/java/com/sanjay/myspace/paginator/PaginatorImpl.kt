package com.sanjay.myspace.paginator

class PaginatorImpl<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> List<Item>,
    private val getNextKey: suspend () -> Key,
    private val onError: suspend (String) -> Unit = {},
    private val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit,
) : Pagination<Key, Item> {
    private var currentKey = initialKey
    private var isMakingRequest = false
    override suspend fun loadNextItems() {
        if (isMakingRequest) {
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        onLoadUpdated(false)
        currentKey = getNextKey()
        onSuccess(result, currentKey)
    }

    override fun reset() {
        currentKey = initialKey
    }
}

interface Pagination<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}