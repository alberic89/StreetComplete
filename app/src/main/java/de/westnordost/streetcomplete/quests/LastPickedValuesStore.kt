package de.westnordost.streetcomplete.quests

import android.content.SharedPreferences
import androidx.core.content.edit

import javax.inject.Inject

import de.westnordost.streetcomplete.Prefs
import de.westnordost.streetcomplete.view.image_select.DisplayItem
import de.westnordost.streetcomplete.view.image_select.GroupableDisplayItem
import kotlin.math.min

/** T must be a string or enum - something that distinctly converts toString. */
class LastPickedValuesStore @Inject constructor(private val prefs: SharedPreferences) {

    fun add(key: String, newValues: Iterable<String>) {
        val lastValues = newValues.asSequence() + get(key)
        prefs.edit {
            putString(getKey(key), lastValues.take(MAX_ENTRIES).joinToString(","))
        }
    }

    fun add(key: String, value: String) = add(key, listOf(value))

    fun get(key: String): Sequence<String> =
        prefs.getString(getKey(key), null)?.splitToSequence(",") ?: sequenceOf()

    private fun getKey(key: String) = Prefs.LAST_PICKED_PREFIX + key
}

private const val MAX_ENTRIES = 100

/* Returns `count` unique items, sorted by how often they appear in the last `historyCount` answers.
 * If fewer than `count` unique items are found, look farther back in the history.
 * Only returns items in `itemPool` ("valid"), although other answers count towards `historyCount`.
 * If there are not enough unique items in the whole history, add unique `defaultItems` as needed.
 * Always include the most recent answer, if it is in `itemPool`, but still sorted normally. So, if
 * it is not one of the `count` most frequent items, it will replace the last of those.
 *
 * impl: null represents items not in the item pool
 */
fun <T> LastPickedValuesStore.getWeighted(
    key: String,
    count: Int,
    historyCount: Int,
    defaultItems: List<GroupableDisplayItem<T>>,
    itemPool: List<GroupableDisplayItem<T>>
): List<GroupableDisplayItem<T>> {
    val stringToItem = itemPool.associateBy { it.value.toString() }
    val lastPickedItems = get(key).map { stringToItem.get(it) }
    return lastPickedItems.mostCommonWithin(count, historyCount).padWith(defaultItems).toList()
}

fun <T : Any> Sequence<T?>.mostCommonWithin(count: Int, historyCount: Int): Sequence<T> {
    val counts = this.countUniqueNonNull(historyCount, count)
    val top = counts.keys.sortedByDescending { counts.get(it) }
    val latest = this.take(1).filterNotNull()
    val items = (latest + top).distinct().take(count)
    return items.sortedByDescending { counts.get(it) }
}

// Counts at least the first `minItems`, keeps going until it finds at least `target` unique values
private fun <T : Any> Sequence<T?>.countUniqueNonNull(minItems: Int, target: Int): Map<T, Int> {
    val counts = mutableMapOf<T, Int>()
    val items = takeAtLeastWhile(minItems) { counts.size < target }.filterNotNull()
    return items.groupingBy { it }.eachCountTo(counts)
}

// Take at least `count` elements, then continue until `predicate` returns false
private fun <T> Sequence<T>.takeAtLeastWhile(count: Int, predicate: (T) -> Boolean): Sequence<T> =
    withIndex().takeWhile{ (i, t) -> i < count || predicate(t) }.map { it.value }

fun <T> LastPickedValuesStore.moveLastPickedDisplayItemsToFront(
    key: String,
    defaultItems: List<DisplayItem<T>>,
    itemPool: List<DisplayItem<T>>
): List<DisplayItem<T>> {
    val stringToItem = itemPool.associateBy { it.value.toString() }
    val lastPickedItems = get(key).mapNotNull { stringToItem.get(it) }
    return lastPickedItems.padWith(defaultItems).toList()
}

private fun <T> Sequence<T>.padWith(defaults: List<T>, count: Int = defaults.size) =
    (this + defaults).distinct().take(count)
