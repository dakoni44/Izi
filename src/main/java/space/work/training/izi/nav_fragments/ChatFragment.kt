package space.work.training.izi.nav_fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.ChatAdapter
import space.work.training.izi.databinding.FragmentChatBinding
import space.work.training.izi.model.Chat
import space.work.training.izi.mvvm.chatList.chat.ChatUser
import space.work.training.izi.mvvm.chatList.chat.ChatViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(), ChatAdapter.OnItemClickListener {

    private lateinit var binding: FragmentChatBinding

    private val args: ChatFragmentArgs by navArgs()
    private var receiverId: String? = null

    private val chatViewModel: ChatViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    var senderId: String? = null

    private var chatList: ArrayList<Chat> = ArrayList()
    private var chatAdapter: ChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiverId = args.uId
        chatViewModel.setReceiverId(receiverId!!)
        chatViewModel.load()

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        binding.rvChat.setHasFixedSize(true)
        binding.rvChat.layoutManager = linearLayoutManager
        chatAdapter = ChatAdapter(requireContext(), this)
        binding.rvChat.setAdapter(chatAdapter)

        senderId = firebaseAuth.currentUser!!.uid


        chatViewModel.getUserChat(receiverId!!).observe(viewLifecycleOwner) {
            it?.let {
                showUser(it)
                loadChatsUi(it)
            }
        }

        binding.bnSend.setOnClickListener {
            val message: String = binding.etMessage.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(message)) {
                chatViewModel.sendMessage(message)
            }
            binding.etMessage.setText("")
        }
    }

    fun showUser(chatUser: ChatUser) {
        binding.tvName.text = chatUser.username
        Glide.with(requireContext()).load(chatUser.img).into(binding.ivProfile)
        chatAdapter!!.setImageUri(chatUser.img)
    }

    fun loadChatsUi(chatUser: ChatUser) {
        chatAdapter!!.setData(chatUser.chatList)
        chatList.clear()
        chatList.addAll(chatUser.chatList)
        binding.rvChat.smoothScrollToPosition(chatAdapter!!.itemCount)
        binding.rvChat.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                binding.rvChat.postDelayed({
                    binding.rvChat.smoothScrollToPosition(
                        chatAdapter!!.itemCount
                    )
                }, 10)
            }
        }
    }

    override fun onItemLongClick(position: Int) {
        if (chatList.get(position).sender.equals(senderId)) {
            val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            alertDialogBuilder.setMessage("Delete message?")
            alertDialogBuilder.setPositiveButton(
                "Ok"
            ) { _, _ ->
                val dbRef = FirebaseDatabase.getInstance().getReference("Chats")
                dbRef.child(chatList.get(position).timestamp).removeValue()
            }
            alertDialogBuilder.setNegativeButton(
                "Cancel"
            ) { arg0, arg1 -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
    }
}