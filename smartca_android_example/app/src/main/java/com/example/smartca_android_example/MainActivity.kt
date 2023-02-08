package com.example.smartca_android_example

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.vnpt.smartca.*

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var vnptSmartCA =  VNPTSmartCASDK()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val btn_click_me = findViewById<Button>(R.id.button)
        val edit_text_trans = findViewById<EditText>(R.id.plain_text_input)
        val btn_create_trans = findViewById<Button>(R.id.button1)
        val btnHomePage = findViewById<Button>(R.id.btnHomePage)

        val config = ConfigSDK()
        config.context = this
        //Cấu hình partnerId, partnerId của đối tác được VNPTSmartCA cung cấp khi yêu cầu tích hợp.
        config.partnerId = "VNPTSmartCAPartner-add1fb94-9629-49`47-b7d8-f2671b04c747"
        //Cấu hình môi trường Dev-test hay Production cùa SmartCA
        config.environment = SmartCAEnvironment.DEMO_ENV
        //Cấu hình ngôn ngữ app (vi/en)
        config.lang = SmartCALanguage.VI
        println( config.lang)

        vnptSmartCA.initSDK(config)

        btn_click_me.setOnClickListener {
            val transId = edit_text_trans.text.toString()

            getAuthentication(transId)
        }

        btn_create_trans.setOnClickListener {
            val transId = edit_text_trans.text.toString()
            getWaitingTransaction(transId)
        }

        btnHomePage.setOnClickListener{
            getMainInfo()
        }
    }

    fun getAuthentication(transId: String) {
        try {
            // SDK tự động xử lý các trường hợp về token: hết hạn, chưa kích hoạt,...
            vnptSmartCA.getAuthentication { result ->
                // Nếu ko lấy được token, credential thì mới show giao diện
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // SDK trả lại token, credential của khách hàng
                        // Đối tác tạo transaction cho khách hàng

                        val obj = Json.decodeFromString(CallbackResult.serializer(), result.data.toString())
                        val token  = obj.accessToken
                        val credentialId = obj.credentialId

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Đã kích hoạt thành công")
                        builder.setMessage("AccessToken: $token; CredentialId: $credentialId")
                        builder.setPositiveButton("Close",
                            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        builder.show()
                        getWaitingTransaction(transId)

                    }
                    else -> {
                        // SDK SmartCA sẽ tự động show giao diện
                    }
                }
            }
        } catch (ex: Exception) {
            throw  ex;
        }
    }

    fun getWaitingTransaction(transId: String) {
        try {
            vnptSmartCA.getAuthentication { result ->
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {

                        // Lấy được token, credential thì mới gọi getWaitingTransaction
                        vnptSmartCA.getWaitingTransaction(transId) { result ->
                            when (result.status) {
                                SmartCAResultCode.SUCCESS_CODE -> {
                                    // Xử lý khi confirm thành công
                                }
                                else -> {
                                    // Xử lý khi confirm thất bại
                                }
                            }
                        }
                    }
                    else -> {
                        //
                    }

                }
            }
        } catch (ex: Exception) {
            throw  ex;
        }
    }

    fun getMainInfo() {
        try {
            vnptSmartCA.getAuthentication { result ->
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        vnptSmartCA.getMainInfo { result ->
                            when (result.status) {
                                SmartCAResultCode.SUCCESS_CODE -> {
                                    // Xử lý khi confirm thành công
                                }
                                else -> {
                                    // Xử lý khi confirm thất bại
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            throw  ex;
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vnptSmartCA.destroySDK()
    }
}

@Serializable
class CallbackResult(val credentialId: String, val accessToken: String)