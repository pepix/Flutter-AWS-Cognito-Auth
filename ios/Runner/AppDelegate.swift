import UIKit
import Flutter
import AWSCore
import AWSCognitoIdentityProvider
import AWSCognito

var pool: AWSCognitoIdentityUserPool?

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    
    
    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?
        ) -> Bool {
        GeneratedPluginRegistrant.register(with: self)
        
        
        let configuration: AWSServiceConfiguration = AWSServiceConfiguration(region: .APNortheast1, credentialsProvider: nil)
        AWSServiceManager.default().defaultServiceConfiguration = configuration
        
        let userPoolConfigration: AWSCognitoIdentityUserPoolConfiguration =
            AWSCognitoIdentityUserPoolConfiguration(
                clientId: "***",
                clientSecret: "***",
                poolId: "***"
            )
        
        AWSCognitoIdentityUserPool.registerCognitoIdentityUserPool(
            with: userPoolConfigration,
            forKey: "AmazonCognitoIdentityProvider"
        )
        
        let controller : FlutterViewController = window?.rootViewController as! FlutterViewController;
        let batteryChannel = FlutterMethodChannel.init(
            name: "aws_cognito_auth.sample/aws",
            binaryMessenger: controller
        );
        
        batteryChannel.setMethodCallHandler({
            (call: FlutterMethodCall, result: FlutterResult) -> Void in
            // Handle battery messages.
            batteryChannel.setMethodCallHandler({
                (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
                if ("login" == call.method) {
                    let argument: NSDictionary
                    argument = call.arguments as! NSDictionary
                    let email = argument["email"] as! String
                    let password = argument["password"] as! String
                    login(email: email, password: password, result: result);
                } else {
                    result(FlutterMethodNotImplemented);
                }
            });
        });
        
        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }
}

private func login(email: String, password: String, result: @escaping FlutterResult) {
    pool = AWSCognitoIdentityUserPool(forKey: "AmazonCognitoIdentityProvider")
    
    let user: AWSCognitoIdentityUser = pool!.getUser(email)
    
    user.getSession(email, password: password, validationData: nil).continueWith(block: {task in
        if((task.error) != nil) {
            result(String(describing: task.error))
        } else {
            result(String(describing: task.result?.accessToken))
        }
        return nil
    })
    
}


