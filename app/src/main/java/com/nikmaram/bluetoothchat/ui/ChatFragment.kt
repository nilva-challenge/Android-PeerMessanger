package com.nikmaram.bluetoothchat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nikmaram.bluetoothchat.R
import com.nikmaram.bluetoothchat.databinding.FragmentChatBinding
import com.nikmaram.bluetoothchat.model.Message
import com.nikmaram.bluetoothchat.util.ChatAdapter
import com.nikmaram.bluetoothchat.util.Constants


class ChatFragment : Fragment(), View.OnClickListener {

    private lateinit var chatInput: EditText
    private lateinit var sendButton: FrameLayout
    private var communicationListener: CommunicationListener? = null
    private var chatAdapter: ChatAdapter? = null
    private lateinit var recyclerviewChat: RecyclerView
    private val messageList = arrayListOf<Message>()
    lateinit var binding: FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        init()
        return binding.root

    }

    private fun init() {


        chatInput = binding.chatInput

        sendButton = binding.sendButton
        recyclerviewChat =binding.chatRecyclerView

        sendButton.isClickable = false
        sendButton.isEnabled = false

        val llm = LinearLayoutManager(activity)
        llm.reverseLayout = true
        recyclerviewChat.layoutManager = llm

        chatInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (s.isNotEmpty()) {
                    sendButton.isClickable = true
                    sendButton.isEnabled = true
                }else {
                    sendButton.isClickable = false
                    sendButton.isEnabled = false
                }
            }
        })
        sendButton.setOnClickListener(this)






        chatAdapter = activity?.let { ChatAdapter(messageList.reversed(), it) }
        recyclerviewChat.adapter = chatAdapter


    }


    override fun onClick(p0: View?) {

        if (chatInput.text.isNotEmpty()){
            communicationListener?.onCommunication(chatInput.text.toString())
            chatInput.setText("")
        }

    }


    fun setCommunicationListener(communicationListener: CommunicationListener){
        this.communicationListener = communicationListener
    }

    interface CommunicationListener{
        fun onCommunication(message: String)
    }

    fun communicate(message: Message){
        messageList.add(message)
        if(activity != null) {
            chatAdapter = ChatAdapter(messageList.reversed(), requireActivity())
            recyclerviewChat.adapter = chatAdapter
            recyclerviewChat.scrollToPosition(0)
        }
    }




}