package de.westnordost.streetcomplete.screens.main.edithistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.westnordost.osmfeatures.FeatureDictionary
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.edithistory.Edit
import de.westnordost.streetcomplete.data.osm.edits.ElementEdit
import de.westnordost.streetcomplete.data.osm.mapdata.Element
import de.westnordost.streetcomplete.ui.common.dialogs.ScrollableAlertDialog
import de.westnordost.streetcomplete.ui.util.toAnnotatedString
import de.westnordost.streetcomplete.util.getNameAndLocationHtml
import de.westnordost.streetcomplete.util.html.parseHtml
import java.text.DateFormat

/** Confirmation dialog for undoing an edit.
 *  Shows details about an edit - time, icon, title, location and what was changed */
@Composable
fun UndoDialog(
    edit: Edit,
    element: Element?,
    featureDictionaryLazy: Lazy<FeatureDictionary>,
    onDismissRequest: () -> Unit,
    onConfirmed: () -> Unit,
) {
    ScrollableAlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.undo_confirm_title2)) },
        content = {
            val context = LocalContext.current

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = DateFormat.getTimeInstance(DateFormat.SHORT).format(edit.createdTimestamp),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.alpha(ContentAlpha.medium)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    EditImage(
                        edit = edit,
                        modifier = Modifier.size(64.dp)
                    )
                    Column {
                        Text(
                            text = edit.getTitle(element?.tags),
                            style = MaterialTheme.typography.body1
                        )
                        if (edit is ElementEdit && element != null) {
                            val nameAndLocation = remember(element, context.resources) {
                                getNameAndLocationHtml(element, context.resources, featureDictionaryLazy.value)
                                    ?.let { parseHtml(it) }
                            }
                            if (nameAndLocation != null) {
                                Text(
                                    text = nameAndLocation.toAnnotatedString(),
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.alpha(ContentAlpha.medium)
                                )
                            }
                        }
                    }
                }
                Divider()
                SelectionContainer {
                    EditDescription(edit)
                }
            }
        },
        buttons = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.undo_confirm_negative))
            }
            TextButton(onClick = { onConfirmed(); onDismissRequest() }) {
                Text(stringResource(R.string.undo_confirm_positive))
            }
        },
        height = 360.dp
    )
}
