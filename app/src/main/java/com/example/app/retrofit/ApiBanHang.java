package com.example.app.retrofit;

import io.reactivex.rxjava3.core.Observable; // Sử dụng Observable từ RxJava thay vì android.database.Observable


import com.example.app.model.CheckModel;
import com.example.app.model.DanhGiaModle;
import com.example.app.model.DonHangModel;
import com.example.app.model.GioHangModel;
import com.example.app.model.ItemModel;
import com.example.app.model.LoaiHangModel;
import com.example.app.model.LoaiSpModel;
import com.example.app.model.MessageModel;
import com.example.app.model.SanPhamMoiModel;
import com.example.app.model.ThongBaoModel;
import com.example.app.model.ThongKeModel;
import com.example.app.model.ThongTinThemModel;
import com.example.app.model.User;
import com.example.app.model.UserModel;
import com.example.app.model.UserModel1;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiBanHang {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();
//    @GET("getspmoi.php")
//    Observable<SanPhamMoiModel> getSpMoi();
    @POST("getspmoi.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSpMoi(
            @Field("iduser") int id
    );
    @POST("getspadmin.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSpAdmin(
            @Field("idshop") int id
    );
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai
    );
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getlaytheoloai(
            @Field("page") int page,
            @Field("loai") int loai,
            @Field("iduser") int iduser
    );
    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid

    );
    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass
    );
    @POST("resetpass.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );
    @POST("donhang.php")
    @FormUrlEncoded
    Observable<MessageModel> createOder(
            @Field("email") String email,
            @Field("sodienthoai") String sodienthoai,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
            );
    @POST("capnhattt.php")
    @FormUrlEncoded
    Observable<UserModel> capnhattt(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("diachi") String diachi,
            @Field("avartar") String avartar
    );
    @POST("dkshop.php")
    @FormUrlEncoded
    Observable<UserModel> dkshop(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("diachi") String diachi
    );
    @POST("capnhattb.php")
    @FormUrlEncoded
    Observable<MessageModel> capNhatTB(
            @Field("id") int id,
            @Field("thongbao") String thongbao
    );
    @POST("xemdonhangshop.php")
    @FormUrlEncoded
    Observable<ItemModel> xemDonHangshop(
            @Field("idshop") int id,
            @Field("trangthai") int trangthai

            );
    @POST("layiduser.php")
    @FormUrlEncoded
    Observable<UserModel> thongtinuser(
            @Field("email") String email
    );
    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id
    );
    @POST("xemdonhanguser.php")
    @FormUrlEncoded
    Observable<ItemModel> xemDonHanguser(
            @Field("iduser") int id,
            @Field("trangthai") int trangthai
    );
    @POST("xemgiohang.php")
    @FormUrlEncoded
    Observable<GioHangModel> xemGioHang(
            @Field("iduser") int id
    );
    @POST("xoagiohang.php")
    @FormUrlEncoded
    Observable<GioHangModel> xoaGioHang(
            @Field("iduser") int iduser
    );
    @POST("capnhatgiohang.php")
    @FormUrlEncoded
    Observable<GioHangModel> capNhatGioHang(
            @Field("iduser") int iduser,
            @Field("idsp") int idsp,
            @Field("soluong") int soluong
            );
    @POST("timkiemmoi.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> timKiem(
            @Field("search") String search,
            @Field("tt") String tt
    );
    @POST("thongke.php")
    @FormUrlEncoded
    Observable<ThongKeModel> thongKe(
            @Field("idshop") int idshop,
            @Field("thang") int thang,
            @Field("nam") int nam
            );
    @POST("xoasanpham.php")
    @FormUrlEncoded
    Observable<MessageModel> xoaSP(
            @Field("idshop") int idshop,
            @Field("idsanpham") int idsp
            );

    @POST("themsp.php")
    @FormUrlEncoded
    Observable<MessageModel> themSP(
            @Field("tensanpham") String tensanpham,
            @Field("loai") int loai,
            @Field("giasp") String giasp,
            @Field("hinhanh") String hinhanh,
            @Field("soluongton") int soluongton,
            @Field("idshop") int idshop,
            @Field("mota") String mota,
            @Field("giamgia") int giamgia

            );
    @POST("suasanpham.php")
    @FormUrlEncoded
    Observable<MessageModel> suaSP(
            @Field("id") int id,
            @Field("tensanpham") String tensanpham,
            @Field("loai") int loai,
            @Field("giasp") String giasp,
            @Field("hinhanh") String hinhanh,
            @Field("soluongton") int soluongton,
            @Field("idshop") int idshop,
            @Field("mota") String mota,
            @Field("giamgia") int giamgia

    );
    @Multipart
    @POST("uploadimage.php")
    Call<MessageModel> uploadFile(@Part MultipartBody.Part file);
    @POST("laythongtinthem.php")
    @FormUrlEncoded
    Observable<ThongTinThemModel> themTT(
            @Field("iddonhang") int id
    );
    @POST("capnhatdonhangshop.php")
    @FormUrlEncoded
    Observable<ThongTinThemModel> capnhatdonhangShop(
            @Field("iddonhang") int iddonhang,
            @Field("idsp") int idsp,
            @Field("trangthai") int trangthai


            );
    @POST("laytoken.php")
    @FormUrlEncoded
    Observable<ThongBaoModel> layTokenChuShop(
            @Field("id") int id


    );
    @POST("getbanchay.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getbanchay(
            @Field("iduser") int id
    );
    @POST("spbanchay.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getspbanchay(
            @Field("iduser") int iduser,
            @Field("page") int page
    );
    @GET("tenloai.php")
    Observable<LoaiHangModel> getloaihang();
    @Multipart
    @POST("uploadimguser.php")
    Call<MessageModel> uploadimguser(@Part MultipartBody.Part file);

    @POST("themdonmomo.php")
    @FormUrlEncoded
    Observable<MessageModel> createOder1(
            @Field("email") String email,
            @Field("sodienthoai") String sodienthoai,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );
    @POST("updatetokenpay.php")
    @FormUrlEncoded
    Observable<MessageModel> themtokenpay(
            @Field("iddonhang") int iddonhang,
            @Field("token") String token
    );
    @POST("huydonhang.php")
    @FormUrlEncoded
    Observable<ThongTinThemModel> huydonhang(
            @Field("iddonhang") int iddonhang,
            @Field("idsp") int idsp


    );
    @POST("hoanthanhdonhang.php")
    @FormUrlEncoded
    Observable<ThongTinThemModel> hoanthanhdonhang(
            @Field("iddonhang") int iddonhang,
            @Field("idsp") int idsp


    );
    @POST("hoanthanhdon.php")
    @FormUrlEncoded
    Observable<ThongTinThemModel> hoanthanhdon(
            @Field("iddonhang") int iddonhang,
            @Field("idsp") int idsp

    );
    @POST("hoanhang.php")
    @FormUrlEncoded
    Observable<ThongTinThemModel> hoanhang(
            @Field("iddonhang") int iddonhang,
            @Field("idsp") int idsp

    );
    @POST("getthongtinshop.php")
    @FormUrlEncoded
    Observable<UserModel> thongtinshop(
            @Field("id") String id
    );
    @POST("danhgiasp.php")
    @FormUrlEncoded
    Observable<UserModel> danhgia(
            @Field("idsp") String idsp,
            @Field("iduser") String iduser,
            @Field("noidung") String noidung
    );
    @POST("laydanhgia.php")
    @FormUrlEncoded
    Observable<DanhGiaModle> laydanhgia(
            @Field("idsp") String idsp
    );
    @POST("dangnhapgoogle.php")
    @FormUrlEncoded
    Observable<UserModel1> dangnhapGoogle(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid,
            @Field("anh") String anh

    );
    @POST("thongkethang.php")
    @FormUrlEncoded
    Observable<ThongKeModel> thongKethang(
            @Field("idshop") int idshop,
            @Field("nam") int nam
    );
    @POST("xoatk.php")
    @FormUrlEncoded
    Observable<UserModel> xoatk(
            @Field("iduser") int iduser
    );
}
