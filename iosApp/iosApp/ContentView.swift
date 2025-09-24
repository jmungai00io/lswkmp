import UIKit
import SwiftUI
import UserNotifications
import ComposeApp

// Initialize Kotlin crash handling
class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        // Set notification delegate to capture taps
        let center = UNUserNotificationCenter.current()
        center.delegate = self
        center.requestAuthorization(options: [.alert, .sound, .badge]) { _, _ in }
        return true
    }

    // Open the file when tapping the notification created by Kotlin code (userInfo contains filePath)
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        if let filePath = response.notification.request.content.userInfo["filePath"] as? String {
            let url = URL(fileURLWithPath: filePath)
            DispatchQueue.main.async {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            }
        }
        completionHandler()
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Add delay to let the runtime initialize properly
        Thread.sleep(forTimeInterval: 0.1)
        
        // Wrap in try-catch at Swift level to prevent crashes
        let viewController: UIViewController
        do {
            viewController = MainViewControllerKt.MainViewController()
        } catch {
            let errorVC = UIViewController()
            let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 100))
            label.text = "Error initializing: \(error.localizedDescription)"
            label.numberOfLines = 0
            label.textAlignment = .center
            errorVC.view.addSubview(label)
            label.center = errorVC.view.center
            return errorVC
        }
        return viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    // Register app delegate for initialization
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
            .onAppear {
            }
    }
}
