package de.westnordost.streetcomplete.screens.settings.quest_selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Surface
import androidx.fragment.app.Fragment
import de.westnordost.streetcomplete.ui.util.composableContent
import org.koin.androidx.viewmodel.ext.android.viewModel

/** Shows a screen in which the user can enable and disable quests as well as re-order them */
class QuestSelectionFragment : Fragment() {

    private val viewModel by viewModel<QuestSelectionViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        composableContent { Surface {
            QuestSelectionScreen(
                viewModel = viewModel,
                onClickBack = { parentFragmentManager.popBackStack() }
            )
        } }
}
