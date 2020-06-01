import 'package:flutter/material.dart';
import 'package:selfsahaf/views/registration/input_field.dart';
import 'package:selfsahaf/views/shopping_cart/credit_card_view/credit_card_form.dart';
import 'package:selfsahaf/views/shopping_cart/order_summary.dart';

import 'package:flutter_credit_card/flutter_credit_card.dart';

import 'credit_card_view/credit_card_model.dart';

class CardInformation extends StatefulWidget {
  @override
  _CardInformationState createState() => _CardInformationState();
}

class _CardInformationState extends State<CardInformation> {
  String cardNumber = '';
  String expiryDate = '';
  String cardHolderName = '';
  String cvvCode = '';
  bool isCvvFocused = false;
  
  TextEditingController _cardholdernameController;
  TextEditingController _cardnumberController;
  TextEditingController _cvcController;
  TextEditingController _expirationController;
  String _cardholdernameValidation(String name) {
    bool emailValid = false;
    if (name.length >= 5) emailValid = true;
    return emailValid ? null : 'not valid name.';
  }
  
  

  @override
  Widget build(BuildContext widget) {
    return Scaffold(
      appBar: AppBar(
        title: Container(
            height: 50, child: Image.asset("images/logo_white/logo_white.png")),
        leading: InkWell(
          child: Icon(
            Icons.arrow_back_ios,
            color: Colors.white,
          ),
          onTap: () {
            Navigator.of(context).pop();
          },
        ),
      ),
      floatingActionButton: GestureDetector(
        child: Padding(
          padding: const EdgeInsets.only(bottom: 8.0),
          child: Container(
            width: MediaQuery.of(context).size.width - 220,
            height: 60,
            child: FlatButton(
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(15.0),
                  side: BorderSide(color: Color.fromRGBO(230, 81, 0, 1))),
              color: Colors.white,
              onPressed: () async {
                //@TODO : degistir bunu
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => OrderSummary()),
                );
              },
              child: Row(
                children: <Widget>[
                  Expanded(
                    flex: 15,
                    child: Text(
                      "Summary",
                      style: TextStyle(
                          color: Color.fromRGBO(230, 81, 0, 1), fontSize: 20),
                    ),
                  ),
                  Expanded(
                    child: Icon(
                      Icons.arrow_forward_ios,
                      color: Color.fromRGBO(230, 81, 0, 1),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
      body: Center(
        child: Container(
          child: Column(
            children: <Widget>[
              Container(
                height: 100,
                child: Center(
                    child: Text(
                  "Card Information",
                  style: TextStyle(color: Colors.white, fontSize: 24),
                )),
              ),
              Divider(
                thickness: 2.5,
                color: Colors.white,
              ),
              CreditCardWidget(
                cardNumber: cardNumber,
                expiryDate: expiryDate,
                cardHolderName: cardHolderName,
                cvvCode: cvvCode,
                showBackView: isCvvFocused,
              ),
              Expanded(
                child: SingleChildScrollView(
                  child: CreditCardForm(
                    onCreditCardModelChange: onCreditCardModelChange,
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
  void onCreditCardModelChange(CreditCardModel creditCardModel) {
    setState(() {
      cardNumber = creditCardModel.cardNumber;
      expiryDate = creditCardModel.expiryDate;
      cardHolderName = creditCardModel.cardHolderName;
      cvvCode = creditCardModel.cvvCode;
      isCvvFocused = creditCardModel.isCvvFocused;
    });
  }
}
