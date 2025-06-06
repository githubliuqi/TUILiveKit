import 'package:barrage/barrage.dart';
import 'package:barrage/state/barrage_store.dart';
import 'package:flutter/material.dart';

class BarrageDisplayWidget extends StatelessWidget {
  final BarrageDisplayController controller;

  const BarrageDisplayWidget({super.key, required this.controller});

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder(
        valueListenable: BarrageStore().state.barrageList,
        builder: (BuildContext context, value, Widget? child) {
          WidgetsBinding.instance.addPostFrameCallback((_) {
            controller.scrollToBottom();
          });
          return Container(
            color: Colors.transparent,
            child: ListView.builder(
              controller: controller.scrollController,
              itemCount: BarrageStore().state.barrageList.value.length,
              shrinkWrap: true,
              padding: EdgeInsets.zero,
              itemBuilder: (context, index) {
                Barrage barrage = BarrageStore().state.barrageList.value[index];
                if (controller.customBarrageBuilder != null && controller.customBarrageBuilder!
                    .shouldCustomizeBarrageItem(barrage)) {
                  return controller.customBarrageBuilder?.buildWidget(context, barrage);
                }
                return BarrageItemWidget(model: barrage);
              },
            ),
          );
        });
  }
}
