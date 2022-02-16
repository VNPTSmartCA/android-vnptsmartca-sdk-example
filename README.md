<h1>Tài liệu tích hợp OneTimeCA SDK Android</h1>	


<font size="4"> **1. Yêu cầu:**</font>

Tối thiểu Android 6.0 (API level 23)

Project sử dụng Java 8

<font size="4"> **2. Hướng dẫn tích hợp:**</font>

<font size="3">**Bước 1:**</font> Tải về bộ tích hợp SDK [tại dây](https://github.com/VNPTSmartCA/android_one_time_ca_sdk/releases) và giải nén ra thư mục.

<font size="3">**Bước 2:**</font> Cấu hình file <span style="color:red"> build.grandle</span> như dưới



```gradle
repositories {
    maven {
        //Đường dẫn đến thư mục giải nén ở bước 1
        url '..path_to_android_one_time_ca_sdk_folder\\repo'
    }
    maven {
        url "https://storage.googleapis.com/download.flutter.io"
    }
}

dependencies {
    // ...
    implementation files('..path_to_android_one_time_ca_sdk_folder\\onetimeca_vnpt_smartca_library.aar')
    debugImplementation 'com.vnpt.vnpt_smartca_sdk.onetime_ca:flutter_debug:1.0'
    releaseImplementation 'com.vnpt.vnpt_smartca_sdk.onetime_ca:flutter_release:1.0'
}
```
<font size="4"> **3. Các chức năng chính:**</font>

* Kích hoạt tài khoản/lấy thông tin xác thực người dùng (accessToken và credentialId)
* Xác nhận giao dịch ký số


Thêm đoạn code dưới đây tại Activity muốn kết nối với SDK trước khi sử dụng các chức năng.
```Kotlin
class MainActivity : AppCompatActivity() {
    var onetimeVNPTSmartCA = OnetimeVNPTSmartCA()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = ConfigSDK()
        config.context = this
        config.partnerId = "VNPTSmartCAPartner-add1fb94-9629-4947-b7d8-f2671b04c747"
        //Cấu hình môi trường Dev-test hay Production cùa SmartCA
        config.environment = SmartCAEnvironment.DEMO_ENV
        //Cấu hình ngôn ngữ app (vi/en)
        config.lang = SmartCALanguage.VI
        onetimeVNPTSmartCA.initSDK(config)

        //....
    }
```

Thêm FlutterActivity trong file  <span style="color:red"> AndroidManifest.xml</span> như dưới

```xml
 <application
 <!-- .....-->
 <activity
            android:name="io.flutter.embedding.android.FlutterActivity"
            android:theme="@style/Theme.Smartca_android_example"
            android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale|layoutDirection|fontScale|screenLayout|density|uiMode"
            android:hardwareAccelerated="true"
            android:windowSoftInputMode="adjustResize"
            />

    </application>

```
<font size="4"> **3.1 Mô tả thuộc tính:**</font>

| Key       |      Kiểu giữ liệu      |  Ghi chú |
|:--------------|:-------------|:------|
| accessToken     |  String | Dữ liệu truyền vào để xác minh khách hàng và bảo mật thông tin|
| creadentialId     |    String   |   Dữ liệu truyền vào API để khởi tạo giao dịch ký số với VNPT SmartCA |


<font size="4"> **3.2 Kích hoạt tài khoản/ lấy accessToken và credentialID:**</font>

```Kotlin
//...
    val btn_click_me = findViewById(R.id.button) as Button

    btn_click_me.setOnClickListener {
        val transId = edit_text_trans.text.toString()
        getAuthentication(transId)
    }
//...

fun getAuthentication(transId: String) {
        try {
            // SDK tự động xử lý các trường hợp về token: hết hạn, chưa kích hoạt,...
            onetimeVNPTSmartCA.getAuthentication { result ->
                // Nếu ko lấy được token, credential thì mới show giao diện
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // SDK trả lại token, credential của khách hàng
                        // Đối tác tạo transaction cho khách hàng

                        val obj = Json.decodeFromString(CallbackResult.serializer(), result.data.toString())
                        val token  = obj.accessToken
                        val credentialId = obj.credentialId

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
```

SDK thực hiện kiểm tra đã có tài khoản kích hoạt hay chưa và trạng thái tài khoản. SDK SmartCA mở giao diện kích hoạt tài khoản trong trường hợp chưa co kích hoạt tài khoản hoặc tài khoản hết hạn. Trong trường hợp đã có tài khoản còn hiệu lực, SDK trả về **accessToken** và **credentialID**.

<font size="4"> **3.3 Xác nhận giao dịch:**</font>

```Kotlin
//...

fun getWaitingTransaction(transId: String) {
        try {
            onetimeVNPTSmartCA.getWaitingTransaction(transId) { result ->
            //Lấy kết quả quá trình xác nhận ký số
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // Xử lý khi confirm thành công
                    }
                    else -> {
                        // Xử lý khi confirm thất bại
                    }
                }
            }
        } catch (ex: Exception) {
            throw  ex;
        }
}
```

App tích hợp gọi hàm **getWaitingTransaction** với tham số là ID của giao dịch, SDK sẽ mở giao diện xác nhận ký số và gọi láy thông tin giao dịch với ID tương ứng.

<font size="4"> **3.4 Hủy kết nối SDK:**</font>

```Kotlin
 override fun onDestroy() {
        super.onDestroy()
        onetimeVNPTSmartCA.destroySDK()
 }
```


#### SmartCAResult

| Tên        | Loại dữ liệu | Mô tả                               |
|------------|--------------|-------------------------------------|
| status     | Int          | Trạng thái của dữ liệu trả về       |
| statusDesc | String       | Mô tả trạng thái của dữ liệu trả về |
| data       | String       | Dữ liệu trả về                      |

#### SmartCAResultCode

| Tên                | Mã lỗi |
|--------------------|--------|
| UNKNOWN_ERROR_CODE | 2      |
| USER_CANCEL_CODE   | 1      |
| SUCCESS_CODE       | 0      |

#### Giải thích các tham số sử dụng

| Tham số  | Mô tả                                                                        |
|----------|------------------------------------------------------------------------------|
| tranId   | ID của giao dịch chờ ký số                                                   |
| clientId | ID được VNPTSmartCA cung cấp khi yêu cầu tích hợp, được gửi kèm trong email. |

#### Bảng mã trạng thái gửi về

| Mã    | Mô tả                                      |
|-------|--------------------------------------------|
| 0     | Success                                    |
| 1     | User rejected                              |
| 2     | Unknown error                              |
| 3     | Device not found                           |
| 4     | Can not sign key challenge                 |
| 5     | PIN fail count                             |
| 6     | KAK Not found                              |
| 7     | PIN Not found                              |
| 8     | Token expired                              |
| 30000 | Client not found in system                 |
| 60000 | Credential not exist                       |
| 60001 | Credential not match identity              |
| 60002 | Credential no result                       |
| 60003 | Credential status invalid                  |
| 61000 | Credential assign key failed               |
| 62000 | Signature transaction not found            |
| 62001 | Signature transaction not match identity   |
| 62002 | Signature transaction expired              |
| 62003 | Signature transaction not waiting          |
| 62010 | Signature data request invalid format      |
| 63000 | Credential sign signer authen failed       |
| 63001 | Credential sign init hash signer failed    |
| 63002 | Credential sign file upload failed         |
| 64000 | Credential sign file not support file type |
| 64001 | Credential acceptance generate file failed |
| 64002 | Credential acceptance transaction exist    |

## Tác giả

VNPT SmartCA Development Team

## Bản quyền ©

[Copyright (c) 2021 VNPTSmartCA](https://github.com/VNPTSmartCA/ios-onetimeca-sdk-example/blob/master/LICENSE).

## Liên hệ - Hỗ trợ

email: smartca.vnptit@gmail.com
