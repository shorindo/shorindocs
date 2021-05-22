Draw.loadPlugin(function(ui) {
window.ui = ui;

    const saveActionName = "docs.save";
    mxResources.parse(`${saveActionName}=Save`);
    ui.actions.addAction(saveActionName, () => {
        console.log("save");
    });

    const menu = ui.menus.get("file");
    const oldFunct = menu.funct;
    menu.funct = function (menu, parent) {
        oldFunct.apply(this, arguments);
        ui.menus.addMenuItems(
            menu,
            [
                saveActionName,
            ],
            parent
        );
    };
});
