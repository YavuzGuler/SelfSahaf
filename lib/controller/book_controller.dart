import 'package:dio/dio.dart';
import 'package:selfsahaf/models/book.dart';
import 'package:selfsahaf/controller/generalServices.dart';

class BookService extends GeneralServices {
  Dio _dio;
  BookService() {
    this._dio = super.dio;
  }

  Future<List<Book>> getBooks(int pageNo, int pageSize,
      {String sortBy = "productID"}) async {
    try {
      Response response = await _dio.get("product/getBooks", queryParameters: {
        "pageNo": pageNo,
        "pageSize": pageSize,
        "sortBy": sortBy
      });
      List<Book> result;
      if (response.statusCode == 200) {
        if (response.data.length != 0) {
          List<dynamic> i = response.data;
          result = i.map((p) => Book.fromJson(p)).toList();
          print("gelen veri uzunluıgu");
          print(result.length);
          return result;
        }
        else{
          print("boş liste");
         return [];
         }
      }

      //_token = response.data["token"];
      result = [null];
      return result;
    } on DioError catch (e) {
      if (e.response != null) {
        print(e.response.data);
        print(e.response.headers);
        print(e.response.request);
        return [null];
      } else {
        // Something happened in setting up or sending the request that triggered an Error
        print(e.request);
        print(e.message);
        return [null];
      }
    }
  }
}