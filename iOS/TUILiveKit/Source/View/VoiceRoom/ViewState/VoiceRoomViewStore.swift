//
//  VoiceRoomViewStore.swift
//  TUILiveKit
//
//  Created by aby on 2024/5/24.
//

import Combine

class VoiceRoomViewStoreFactory: StoreFactory {

    typealias T = VoiceRoomViewStoreProvider
    static var storeMap: [String : T] = [:]
    
    static func getStore(roomId: String) -> T {
        if let liveRoomViewStore = storeMap[roomId] {
            return liveRoomViewStore
        }
        let liveRoomViewStore = T()
        storeMap.updateValue(liveRoomViewStore, forKey: roomId)
        return liveRoomViewStore
    }
}

protocol VoiceRoomViewStore {
    var viewActionSubject: PassthroughSubject<any IdentifiableAction, Never> { get }
    
    func dispatch(action: Action)
    func select<Value: Equatable>(_ selector: Selector<VoiceRoomViewState, Value>) -> AnyPublisher<Value, Never>
    func selectCurrent<Value>(_ selector: Selector<VoiceRoomViewState, Value>) -> Value
}

class VoiceRoomViewStoreProvider {
    private(set) lazy var store = Store(initialState: VoiceRoomViewState())
    let viewActionSubject = PassthroughSubject<any IdentifiableAction, Never>()
    init() {
        initializeViewStore()
    }
    
    private func initializeViewStore() {
        store.register(reducer: voiceRoomMenuReducer, for: \VoiceRoomViewState.menu)
    }
}

extension VoiceRoomViewStoreProvider: VoiceRoomViewStore {
    func dispatch(action: any Action) {
        guard let action = action as? IdentifiableAction else { return }
        if action.id.contains(VoiceRoomViewActions.key) {
            store.dispatch(action: action)
        } else if action.id.hasPrefix(VoiceRoomViewResponseActions.key) {
            viewActionSubject.send(action)
        }
    }
    
    func select<Value>(_ selector: Selector<VoiceRoomViewState, Value>) -> AnyPublisher<Value, Never> where Value : Equatable {
        return store.select(selector)
    }
    
    func selectCurrent<Value>(_ selector: Selector<VoiceRoomViewState, Value>) -> Value {
        return store.selectCurrent(selector)
    }
}
