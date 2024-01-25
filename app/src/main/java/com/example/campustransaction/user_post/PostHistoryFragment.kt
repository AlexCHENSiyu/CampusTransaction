package com.example.campustransaction.user_post

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentPostHistoryBinding
import com.example.campustransaction.ui.UIViewModel
import com.example.campustransaction.user_post.adapter.PostAdapter


class PostHistoryFragment : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentPostHistoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPostHistoryBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.sdkPostHistory()

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))

        viewModel.responsePostHistory.observe(viewLifecycleOwner){
            if (viewModel.responsePostHistory.value?.Success == true){
                val postList = viewModel.responsePostHistory.value!!.Posts
                binding.recyclerView.adapter = context?.let { it1 -> postList?.let { it2 -> PostAdapter(it1, it2) } }
                binding.recyclerView.setHasFixedSize(false)

                (binding.recyclerView.adapter as PostAdapter?)?.setOnItemClickListener(object: PostAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        //Toast.makeText(context, "click $position item", Toast.LENGTH_SHORT).show()
                        viewModel.postDetail = viewModel.responsePostHistory.value!!.Posts?.get(position)
                        view.findNavController().navigate(R.id.action_postHistoryFragment_to_postDetailFragment)
                    }
                    override fun onItemLongClick(view: View, position: Int) {}
                })
            }
        }

        return binding.root
    }

}

class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

//        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildAdapterPosition(view) == 0) {
//            outRect.top = space
//        } else {
//            outRect.top = 0
//        }
    }
}