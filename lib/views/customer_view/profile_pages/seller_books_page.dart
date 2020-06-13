import 'package:Selfsahaf/controller/product_services.dart';
import 'package:Selfsahaf/models/book.dart';
import 'package:Selfsahaf/views/customer_view/products_pages/book_profile.dart';
import 'package:Selfsahaf/views/customer_view/products_pages/product_card.dart';
import 'package:Selfsahaf/views/errors/error_dialog.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';

class SellerBooksPage extends StatefulWidget {
  Book bookfrom;
  SellerBooksPage({this.bookfrom});
  @override
  _SellerBooksPage createState() => new _SellerBooksPage();
}

class _SellerBooksPage extends State<SellerBooksPage> {
  final GlobalKey<RefreshIndicatorState> _refreshIndicatorKey =
      new GlobalKey<RefreshIndicatorState>();
  ProductService _productService = GetIt.I<ProductService>();
  List<Book> sellerBooks;

  Book bookfrom;
  _getSellerBooks(BuildContext context) async {
    _productService.getSellerBooks(this.bookfrom.sellerID).then((value) {
      print(value.data);
      if (!value.error) {
        setState(() {
          this.sellerBooks = value.data;
        });
      } else {
        ErrorDialog().showErrorDialog(context, "Error", value.errorMessage);
        setState(() {
          this.sellerBooks = value.data;
        });
      }
    });
  }

  @override
  void initState() {
    this.bookfrom = widget.bookfrom;
    _getSellerBooks(context);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: RefreshIndicator(
            onRefresh: () => _getSellerBooks(context),
            key: _refreshIndicatorKey,
            child: ListView(
              children: <Widget>[
                ListView.builder(
                  physics: NeverScrollableScrollPhysics(),
                  shrinkWrap: true,
                  itemBuilder: (BuildContext context, index) {
                    return (sellerBooks == null)
                        ? Center(
                            child: Text(
                              "This Seller Has No Books On Sale.",
                              style:
                                  TextStyle(color: Colors.white, fontSize: 25),
                              textAlign: TextAlign.center,
                            ),
                          )
                        : Padding(
                            padding: const EdgeInsets.all(16.0),
                            child: Column(
                              children: <Widget>[
                                GestureDetector(
                                  child: ProductCard(
                                    authorName:
                                        this.sellerBooks[index].authorName,
                                    bookName: this.sellerBooks[index].name,
                                    productID:
                                        this.sellerBooks[index].productID,
                                    price: this
                                        .sellerBooks[index]
                                        .price
                                        .toString(),
                                    sellerID: this.sellerBooks[index].sellerID,
                                    publisherName:
                                        this.sellerBooks[index].publisher,
                                    amount: this.sellerBooks[index].quantity,
                                    type: 0,
                                  ),
                                  onTap: () {
                                    Navigator.push(
                                        context,
                                        MaterialPageRoute(
                                            builder: (context) => BookProfile(
                                                  selectedBook:
                                                      sellerBooks[index],
                                                  type: 0,
                                                )));
                                  },
                                ),
                              ],
                            ),
                          );
                  },
                  itemCount: (sellerBooks == null) ? 1 : sellerBooks.length,
                ),
                SizedBox(
                  height: 80,
                )
              ],
            )));
  }
}
