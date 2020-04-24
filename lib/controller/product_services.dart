import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:selfsahaf/controller/generalServices.dart';
import 'package:selfsahaf/models/api_response.dart';
import 'package:selfsahaf/models/book.dart';
import 'package:selfsahaf/models/category.dart';

class ProductService extends GeneralServices {
  Dio _dio;
  Response response;
  APIResponse<int> apiresponse;
  ProductService() {
    this._dio = super.dio;
  }

  Future<int> addBook(Book book, int sellerID) async {
    try {
      Response response = await _dio.post("product/addBook",
          queryParameters: {
            "price": book.price,
            "quantity": book.quantity,
            "sellerID": sellerID
          },
          data: json.encode(book.toJsonBook()));
      return response.statusCode;
    } on DioError catch (e) {
      if (e.response != null) {
        print(e.response.data);
        print(e.response.headers);
        print(e.response.statusCode);
        return e.response.statusCode;
      } else {
        // Something happened in setting up or sending the request that triggered an Error
        print(e.request);
        print(e.message);
        return null;
      }
    }
  }
Future<List<Book>> getSelfBooks() async {
 try {
      Response response =
          await _dio.get("product/getSelfBooks");
          List<Book> result;
        if (response.statusCode == 200) {
          if(response.data.length!=0){
            print("if");
             print(response.statusCode);
        print(response.data);
          List<dynamic> i = response.data;
          result = i.map((p) => Book.fromJson(p)).toList();
          return result;
          }
        }
        print(response.statusCode);
        print(response.data.length);
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
Future<List<Category>> getCategories() async {
 try {
      Response response =
          await _dio.get("product/getCategories");
          List<Category> result=[null];
        if (response.statusCode == 200) {
            print("if");
             print(response.statusCode);
        print(response.data);
          List<dynamic> i = response.data;
          result = i.map((p) => Category.fromJson(p)).toList();
          return result;
        }
        print(response.statusCode);
        print(response.data.length);
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
Future<bool> deleteBook(int bookID) async{
  try{
    Response response = await _dio.delete("product/deleteBook",
    queryParameters: {
      "productId":bookID
    });
    print(response.statusCode);
      return (response.statusCode==200)?true:false;

  } on DioError catch(e){
         if (e.response != null) {
        print(e.response.data);
        print(e.response.headers);
        print(e.response.request);
        return false;
      } else {
        // Something happened in setting up or sending the request that triggered an Error
        print(e.request);
        print(e.message);
        return false;
      }
  }

}

}
