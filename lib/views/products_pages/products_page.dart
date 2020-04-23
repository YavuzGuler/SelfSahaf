import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:selfsahaf/views/products_pages/add_book.dart';
import 'package:selfsahaf/views/products_pages/product_card.dart';
import 'package:selfsahaf/controller/product_services.dart';
import "package:selfsahaf/models/book.dart";

class ProductsPage extends StatefulWidget {
  ProductsPage({Key key}) : super(key: key);

  @override
  _ProductsPageState createState() => _ProductsPageState();
}

class _ProductsPageState extends State<ProductsPage> {
  ProductService get _productService => GetIt.I<ProductService>();
  List<Book> bookList;
  final TextEditingController _filter = new TextEditingController();
  List names = new List();
  List books = new List();
  bool _isloading=false;
  @override
  void initState() {
    _getSelfBooks();

  }
_getSelfBooks() async{
  setState(() {
    _isloading=true;
  });
  bookList= await _productService.getSelfBooks();
  setState(() {
    _isloading=false;
  });
  print(bookList.length);

}
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        elevation: 13,
        leading: InkWell(
          child: Icon(Icons.arrow_back),
          onTap: () {
            Navigator.pop(context);
          },
        ),
        title: Padding(
          padding: const EdgeInsets.all(3.0),
          child: TextField(
            style: TextStyle(
              color: Colors.black,
            ),
            controller: _filter,
            decoration: new InputDecoration(
                enabledBorder: UnderlineInputBorder(
                  borderSide: BorderSide(
                    color: Colors.white,
                  ),
                ),
                focusedBorder: UnderlineInputBorder(
                  borderSide: BorderSide(
                    color: Colors.white,
                  ),
                ),
                prefixIcon: InkWell(
                    child: new Icon(
                  Icons.search,
                  color: Colors.white,
                )),
                hintText: 'Ara...',
                hintStyle: TextStyle(
                  color: Colors.white,
                )),
          ),
        ),
        actions: <Widget>[
          IconButton(
              icon: Icon(Icons.add_box),
              onPressed: () {
                Navigator.push(context,
                    MaterialPageRoute(builder: (context) => AddBook()));
              }),
        ],
      ),
      body: (_isloading)? CircularProgressIndicator() :Container(
        color: Color(0xffe65100),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: (bookList == [null])
              ? Center(
                  child: Text("No book"),
                )
              : ListView.builder(
                  itemCount: bookList.length,
                  itemBuilder: (BuildContext context, int index) {
                    return ProductCard(
                      bookName: bookList[index].name,
                      authorName: bookList[index].authorName,
                      publisherName: bookList[index].publisher,
                      price: "${bookList[index].price}",
                      
                    );
                  },
                ),
        ),
      ),
      floatingActionButton: FilterFloating(),
    );
  }
}

class FilterFloating extends StatefulWidget {
  @override
  _FilterFloatingState createState() => _FilterFloatingState();
}

class _FilterFloatingState extends State<FilterFloating> {
  bool _show = true;
  @override
  Widget build(BuildContext context) {
    return _show
        ? FloatingActionButton(
            backgroundColor: Colors.white,
            child: Icon(
              Icons.lightbulb_outline,
              color: Color(0xffe65100),
            ),
            onPressed: () {
              var sheetController = showBottomSheet(
                context: context,
                builder: (context) => Container(
                  decoration: BoxDecoration(
                      color: Colors.transparent,
                      borderRadius: BorderRadius.only(
                          topLeft: const Radius.circular(20),
                          topRight: const Radius.circular(20))),
                  child: Container(
                    height: 450,
                    child: Center(
                      child: Column(
                        children: <Widget>[
                          Expanded(
                            child: FlatButton(
                              onPressed: () {},
                              child: Icon(
                                Icons.close,
                                color: Colors.white,
                              ),
                              color: Colors.black,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              );
              _showButton(false);

              sheetController.closed.then((value) {
                _showButton(true);
              });
            },
          )
        : Container();
  }

  void _showButton(bool value) {
    setState(() {
      _show = value;
    });
  }
}
