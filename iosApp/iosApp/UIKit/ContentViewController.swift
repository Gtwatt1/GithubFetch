//
//  ContentViewController.swift
//  iosApp
//
//  Created by Godwin Olorunshola on 2024-12-08.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit
import RxSwift
import shared
import RxCocoa
import SwiftUI

class ContentViewController: UIViewController {

    private let disposeBag = DisposeBag()
    
    private let searchBar = UISearchBar()
    private let tableView = UITableView()
    private let activityIndicator = UIActivityIndicatorView(style: .large)
    
    private var viewModel = ContentUIKitViewModel()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        bindViewModel()
    }

    private func setupUI() {
        view.backgroundColor = .white

        searchBar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(searchBar)

        tableView.translatesAutoresizingMaskIntoConstraints = false
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false

        tableView.register(UserCell.self, forCellReuseIdentifier: UserCell.identifier)
        view.addSubview(tableView)
        view.addSubview(activityIndicator)

        NSLayoutConstraint.activate([
            searchBar.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            searchBar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            searchBar.trailingAnchor.constraint(equalTo: view.trailingAnchor),

            tableView.topAnchor.constraint(equalTo: searchBar.bottomAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            activityIndicator.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            activityIndicator.centerYAnchor.constraint(equalTo: view.centerYAnchor)

        ])
        title = "User List"
    }

    private func bindViewModel() {
        searchBar.rx.text.orEmpty
            .bind(to: viewModel.searchQuery)
            .disposed(by: disposeBag)

        viewModel.users
            .bind(to: tableView.rx.items(cellIdentifier: UserCell.identifier, cellType: UserCell.self)) { index, user, cell in
                cell.configure(with: user)
            }
            .disposed(by: disposeBag)

        viewModel.isLoading
            .bind(to: activityIndicator.rx.isAnimating)
            .disposed(by: disposeBag)

        viewModel.error
            .subscribe(onNext: {[weak self] error in
                self?.showErrorAlert(error: error)
            })
            .disposed(by: disposeBag)
    }
    
    private func showErrorAlert(error: String) {
        let alert = UIAlertController(title: "Error", message: error, preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        
        present(alert, animated: true, completion: nil)
    }
}

struct ContentViewControllerWrapper: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> ContentViewController {
        return  ContentViewController()
    }
    
    func updateUIViewController(_ uiViewController: ContentViewController, context: Context) {
        // Any updates to the UIViewController can be handled here
    }
}
