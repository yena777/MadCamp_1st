package com.example.first_app

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.first_app.databinding.FragmentPostListBinding
import com.example.first_app.databinding.FragmentReadBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ReadFragment : Fragment() {
    var key : String ?= null
    private var mBinding: FragmentReadBinding?= null
    private val binding get() = mBinding!!
    val database = Firebase.database
    val myRef = database.getReference("board")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    // Context를 액티비티로 형변환해서 할당
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    //PostList data 받아오기
    fun getPostData(key : String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val item = dataSnapshot.getValue(Model::class.java)
                //Log.e("title: ", item!!.title)
                if(item != null){
                    binding.tvTitle.text = item!!.title
                    binding.tvBody.text = item!!.body
                    binding.tvTime.text = item!!.time
                    binding.tvEmail.text = item!!.email
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("PostListFragment", "Failed to read value.", error.toException())

            }
        }
        myRef.child(key).addValueEventListener(postListener)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        key = requireArguments().getString("key")
        Log.e("receive key: ", key.toString())
        mBinding = FragmentReadBinding.inflate(inflater, container, false)

        getPostData(key.toString())
        showDialog(key.toString())

        binding.btnUpdate.setOnClickListener{
            val updatePostFragment = UpdateFragment()
            val bundle = Bundle()
            bundle.putString("key", key)
            updatePostFragment.arguments = bundle
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_container, updatePostFragment, "updatePost")
                .commit();
        }

        return binding.root
    }

    override fun onDestroyView() {
        //onDestroyView 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroyView()
    }

    fun showDialog(key : String){
        // 기본 형태의 다이얼로그
        binding.btnDelete.setOnClickListener {
            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(mainActivity)
            builder.setTitle("게시글 삭제")
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        val postListFragment = PostListFragment()
                        requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_container, postListFragment, "postList")
                            .commit()
                        database.getReference("board").child(key.toString()).removeValue()
                        Toast.makeText(mainActivity, "삭제 완료", Toast.LENGTH_LONG).show()
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReadFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}