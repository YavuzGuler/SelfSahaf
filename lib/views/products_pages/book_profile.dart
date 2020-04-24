import 'package:flutter/material.dart';
import 'package:selfsahaf/views/page_classes/main_page/home_page_carousel.dart';
import 'package:selfsahaf/models/book.dart';

class BookProfile extends StatefulWidget {
  Book selectedBook;
  BookProfile({@required this.selectedBook});
  @override
  _BookProfileState createState() => _BookProfileState();
}

class _BookProfileState extends State<BookProfile> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        elevation: 12,
       
        title: Container(
            height: 50, child: Image.asset("images/logo_white/logo_white.png")),
        actions: <Widget>[
          IconButton(icon: Icon(Icons.settings), onPressed: () {})
        ],
      ),
      body: Center(
        child: Container(
          color: Color(0xffe65100),
          child: Column(
            children: <Widget>[
              HomePageCarousel(),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Row(
                  children: <Widget>[
                    Expanded(
                      flex: 20,
                      child: Container(
                        width: double.maxFinite,
                        height: 45,
                        child: Center(
                            child: Text(
                          widget.selectedBook.name,
                          style: TextStyle(color: Colors.white),
                        )),
                      ),
                    ),
                    Expanded(flex: 2, child: SizedBox()),
                    Expanded(
                      flex: 6,
                      child: Container(
                        width: double.maxFinite,
                        height: 45,
                        child: Center(
                            child: Text(
                         " ${widget.selectedBook.price} TL",
                          style: TextStyle(color: Colors.white),
                        )),
                      ),
                    )
                  ],
                ),
              ),
              Divider(
                thickness: 3.0,
                color: Colors.white,
              ),
              Expanded(
                child: SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.only(left: 20, right: 20),
                    child: Column(
                      children: <Widget>[
                        SizedBox(
                          height: 5,
                        ),
                        Container(
                          width: double.maxFinite,
                          height: 45,
                          child: Center(
                            child: Row(
                              children: <Widget>[
                                Text(
                                  "Yazar Adi: ",
                                  style: TextStyle(color: Colors.white),
                                ),
                                Text(widget.selectedBook.authorName,style: TextStyle(color: Colors.white))
                              ],
                            ),
                          ),
                        ),
                        SizedBox(
                          height: 5,
                        ),
                        Container(
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(15),
                          ),
                          width: double.maxFinite,
                          height: 45,
                          child: Center(
                              child: Text(
                            "Yayinevi Adi",
                            style: TextStyle(color: Color(0xffe65100)),
                          )),
                        ),
                        SizedBox(
                          height: 5,
                        ),
                        Container(
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(15),
                          ),
                          width: double.maxFinite,
                          height: 120,
                          child: Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: Center(
                              child: Column(
                                children: <Widget>[
                                  Expanded(
                                    child: SingleChildScrollView(
                                        child: Text(
                                      widget.selectedBook.description,
                                      style:
                                          TextStyle(color: Color(0xffe65100)),
                                    )),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}