import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:selfsahaf/views/customer_view/main_view/page_classes/main_page/book_card.dart';
import 'package:selfsahaf/views/customer_view/main_view/page_classes/main_page/home_page_carousel.dart';
import 'package:selfsahaf/views/customer_view/main_view/page_classes/main_page/home_page_.dart';
import 'package:selfsahaf/views/customer_view/main_view/page_classes/main_page/sahaf_drawer.dart';
import 'package:selfsahaf/views/customer_view/main_view/page_classes/notification_pages/notifications.dart';
import 'package:selfsahaf/views/customer_view/main_view/page_classes/search_pages/search_page.dart';
import 'package:selfsahaf/views/customer_view/profile_pages/profile_page.dart';
import 'package:selfsahaf/views/customer_view/shopping_cart/shopping_cart.dart';
import 'package:selfsahaf/controller/user_controller.dart';
import 'package:selfsahaf/models/book.dart';
import 'package:selfsahaf/controller/book_controller.dart';

class MainPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _MainPageState();
  }
}

class _MainPageState extends State<MainPage> {
  final GlobalKey<RefreshIndicatorState> _refreshIndicatorKey =
      new GlobalKey<RefreshIndicatorState>();
  ScrollController _scrollController = new ScrollController();
  List<Book> bookList = [null];
  bool _isloading = true;
  int _index = 0;
  int page = 0, size = 4, localpage = 0;
  bool checkPage;
  List<Widget> _pages= [
    HomePage(),
    SearchPage(),
    NotificationsPage(),
    ProfilePage(),
  ];
  AuthService get userService => GetIt.I<AuthService>();
  BookService get _bookService => GetIt.I<BookService>();

  bool _loading = true;
  @override
  void initState() {

    _fetchData();

    _pages = [
      MainPage(),
      SearchPage(),
      NotificationsPage(),
      ProfilePage()
    ];
    _scrollController.addListener(() {
      if (_scrollController.position.pixels ==
          _scrollController.position.maxScrollExtent) {
        _getMoreData();
      }

    });
    
    
  }

  _getMoreData() async {
    setState(() {
      _isloading = true;
    });

    print("page: $page");
    List<Book> newbooks;
    newbooks = await _bookService.getBooks(page, size);
    setState(() {
      if (newbooks.length == 0) {
        _isloading = false;
        return;
      } else if (bookList.length % size != 0 || newbooks.length != size) {
        for (int i = (bookList.length % 4); i < newbooks.length; i++) {
          bookList.add(newbooks[i]);
        }
        if (newbooks.length == size) page += 1;
      } else if (bookList.length % size == 0 &&
          newbooks.length == size &&
          (bookList[bookList.length - 1].productID !=
              newbooks[size - 1].productID)) {
        print("salam");
        bookList.addAll(newbooks);
        page = page + 1;
      }

      _isloading = false;
    });
  }

  _fetchData() async {
     userService.initUser().then((e) {
       _refresh().then((value){
         setState(() {
            _loading = false;
          });
       });
          
        });
  }

  Future<Null> _refresh() {
    setState(() {
      _isloading = true;
    });
    return _bookService.getBooks(0, 4).then((e) {
      setState(() {
        if (e.length % 4 == 0)
          page = 1;
        else
          page = 0;
      
        this.bookList = e;
          _isloading = false;
      });
      print(bookList.length);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        bottomNavigationBar: BottomAppBar(
          elevation: 5,
          child: Container(
            decoration: BoxDecoration(
              color: Color(0xffe65100),
            ),
            height: 60,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                Column(
                  children: <Widget>[
                    InkWell(
                      child: _index == 0
                          ? Icon(
                              Icons.home,
                              size: 35,
                              color: Colors.white,
                            )
                          : Icon(
                              Icons.home,
                              size: 30,
                              color: Colors.white,
                            ),
                      onTap: () {
                        _changeIndex(0);
                      },
                    ),
                    _index == 0
                        ? Text(
                            "Ana Sayfa",
                            style: TextStyle(
                                fontSize: 10, fontWeight: FontWeight.bold),
                          )
                        : SizedBox(),
                  ],
                ),
                Column(
                  children: <Widget>[
                    InkWell(
                      child: _index == 1
                          ? Icon(
                              Icons.search,
                              size: 25,
                              color: Colors.white,
                            )
                          : Icon(
                              Icons.search,
                              size: 30,
                              color: Colors.white,
                            ),
                      onTap: () {
                        _changeIndex(1);
                      },
                    ),
                    _index == 1
                        ? Text(
                            "Arama",
                            style: TextStyle(
                                fontSize: 10, fontWeight: FontWeight.bold),
                          )
                        : SizedBox(),
                  ],
                ),
                Column(
                  children: <Widget>[
                    InkWell(
                      child: _index == 2
                          ? Icon(
                              Icons.notifications,
                              size: 25,
                              color: Colors.white,
                            )
                          : Icon(
                              Icons.notifications_none,
                              size: 30,
                              color: Colors.white,
                            ),
                      onTap: () {
                        _changeIndex(2);
                      },
                    ),
                    _index == 2
                        ? Text(
                            "Bildirimler",
                            style: TextStyle(
                                fontSize: 10, fontWeight: FontWeight.bold),
                          )
                        : SizedBox(),
                  ],
                ),
                Column(
                  children: <Widget>[
                    InkWell(
                      child: _index == 3
                          ? Icon(
                              Icons.person,
                              size: 25,
                              color: Colors.white,
                            )
                          : Icon(
                              Icons.person_outline,
                              size: 30,
                              color: Colors.white,
                            ),
                      onTap: () {
                        _changeIndex(3);
                      },
                    ),
                    _index == 3
                        ? Text(
                            "Profilim",
                            style: TextStyle(
                                fontSize: 10, fontWeight: FontWeight.bold),
                          )
                        : SizedBox(),
                  ],
                ),
              ],
            ),
          ),
        ),
        
        
        body: _pages[_index],);
  }

  _changeIndex(int i) {
    _index = i;
    setState(() {});
  }
}
