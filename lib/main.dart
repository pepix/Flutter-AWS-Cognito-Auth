import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'dart:async';

void main() => runApp(new MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Flutter AWS Sample',
      theme: new ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: new MyHomePage(title: 'login'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => new _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel('aws_cognito_auth.sample/aws');

  // Get Login Status
  String _loginstatus = 'Unknown login status';

  String _username = "mailaddress";
  String _password = "password";

  Future<Null> _login() async {
    print("login");
    String resultMessage;
    try {
      final String _resultMessage = await platform.invokeMethod(
          'login',
          <String, dynamic>{
            "email": _username,
            "password": _password
          }
      );
      resultMessage = '${_resultMessage}';
    } on PlatformException catch (e) {
      resultMessage = "Failed to login: '${e.message}'";
    } catch (e) {
      print("Error: '${e}'");
    }

    setState(() {
      print("set state!");
      print(resultMessage);
      _loginstatus = resultMessage;
    });
  }

  @override
  Widget build(BuildContext context) {
    final key = new GlobalKey<ScaffoldState>();

    return new Material(
      key: key,
      child: new Center(
        child: new ListView(
          children: <Widget>[
            new Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                new Container(
                  child: new TextField(
                    decoration: new InputDecoration(
                        hintText: "Email"
                    ),
                    onChanged: (String string) {
                      _username = string;
                    },
                  ),
                  margin: const EdgeInsets.all(40.0),
                ),
                new Container(
                  child: new TextField(
                    decoration: new InputDecoration(
                        hintText: "password"
                    ),
                    onChanged: (String string) {
                      _password = string;
                    },
                  ),
                  margin: const EdgeInsets.all(40.0),
                ),
                new RaisedButton(
                  child: new Text("LOGIN"),
                  onPressed: _login,
                ),
                new Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    new GestureDetector(
                      child: new Text(_loginstatus),
                      onLongPress: () {
                        Clipboard.setData(new ClipboardData(text: _loginstatus));
                        key.currentState.showSnackBar(
                          new SnackBar(content: new Text("Copied to Clipboard"))
                        );
                      },
                    ),
                  ]
                ),
                //new Text(_loginstatus),
              ],
            ),
          ],
        )
      )
    );
  }
}
