//
//  ContentUIKitViewModel.swift
//  iosApp
//
//  Created by Godwin Olorunshola on 2024-12-08.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import RxSwift
import RxCocoa
import shared
import KMPNativeCoroutinesRxSwift

class ContentUIKitViewModel {
    private let disposeBag = DisposeBag()
    let koinApp: KoinApp = KoinApp()

    private let _users = BehaviorRelay<[User_]>(value: [])
    var users: Observable<[User_]> {
        return _users.asObservable()
    }

    private let _error = PublishSubject<String>()
    var error: Observable<String> {
        return _error.asObservable()
    }

    private let _loading = BehaviorRelay<Bool>(value: false)
    var isLoading: Observable<Bool> {
        return _loading.asObservable()
    }

    let searchQuery = PublishRelay<String>()
    
    init() {
        setupBindings()
    }

    private func setupBindings() {
        searchQuery
            .debounce(RxTimeInterval.milliseconds(500), scheduler: MainScheduler.instance)
            .filter{ !$0.isEmpty }
            .distinctUntilChanged()
            .flatMapLatest { [weak self] query in
                self?.fetchUsers(query: query, page: 1, perPage: 20) ?? .empty()
            }
            .subscribe(onNext: { [weak self] users in
                self?._users.accept(users)
            }, onError: { [weak self] error in
                self?._error.onNext(error.localizedDescription)
            })
            .disposed(by: disposeBag)
    }
    
    private func fetchUsers(query: String, page: Int, perPage: Int) -> Observable<[User_]> {
        _loading.accept(_users.value.isEmpty)

        let requestObservable = createObservable(
            for: koinApp.getUsers(query: query, page: Int32(page), perPage: Int32(perPage)))
        
        return requestObservable
            .do(onDispose: { [weak self] in
                self?._loading.accept(false)
            })
            .catch { [weak self] error in
                self?._error.onNext(error.localizedDescription)
                return .empty()
            }
    }
  
}
