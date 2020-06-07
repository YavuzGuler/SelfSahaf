import 'package:flutter/material.dart';
import 'package:selfsahaf/views/admin_view/admin_search.dart';
import 'package:selfsahaf/views/admin_view/admin_shipping_companies.dart';
import 'package:selfsahaf/views/admin_view/edit_categories.dart';

class AdminDrawer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(right: 100.0),
      child: Container(
        decoration: BoxDecoration(
          color: Color.fromRGBO(192, 72, 46, 1),
        ),
        child: Column(
          children: <Widget>[
            SizedBox(
              height: 10,
            ),
            Expanded(
              flex: 5,
              child: Padding(
                padding: const EdgeInsets.only(top:36.0),
                child: Container(
                  height: 100,
                  padding: EdgeInsets.only(top: 10, bottom: 0),
                  margin: EdgeInsets.only(left: 50, top: 0, right: 50),
                  child: Center(
                    child: Image.asset('images/selfadmin_logo/selfadmin.png'),
                  ),
                ),
              ),
            ),
            Expanded(
              flex: 3,
              child: InkWell(
                
                child: ListTile(
                    leading: Icon(Icons.search, color: Colors.white),
                    title: Text(
                      "Search \nBooks/Users",
                      style: TextStyle(fontSize: 20, color: Colors.white),
                    )),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => AdminSearch()),
                  );
                },
              ),
            ),
            Expanded(
              flex: 3,
              child: InkWell(
                child: ListTile(
                    leading: Icon(
                      Icons.local_shipping,
                      color: Colors.white,
                    ),
                    title: Text(
                      "Shipping Companies",
                      style: TextStyle(fontSize: 20, color: Colors.white),
                    )),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => ShippingCompanies()),
                  );
                },
              ),
            ),
            Expanded(
              flex: 3,
              child: InkWell(
                child: ListTile(
                    leading: Icon(Icons.edit, color: Colors.white),
                    title: Text(
                      "Edit Categories",
                      style: TextStyle(fontSize: 20, color: Colors.white),
                    )),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => EditCategories()),
                  );
                },
              ),
            ),
            Expanded(
              flex: 25,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.end,
                children: <Widget>[
                  FlatButton(
                    shape: new RoundedRectangleBorder(
                        borderRadius: new BorderRadius.circular(15.0),
                        side: BorderSide(color: Color.fromRGBO(230, 81, 0, 1))),
                    color: Colors.white,
                    onPressed: () {
                      // Navigator.push(
                      //   context,
                      //   MaterialPageRoute(builder: (context) => Signup()),
                      // );
                    },
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(
                        "Update Hotlist",
                        style: TextStyle(
                            color: Color.fromRGBO(230, 81, 0, 1), fontSize: 20),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            SizedBox(
              height: 20,
            ),
          ],
        ),
      ),
    );
  }
}