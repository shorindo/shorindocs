package com.shorindo.docs;

import com.shorindo.docs.action.ActionMessages;

public enum ApplicationMessages implements ActionMessages {
	@Message(lang = "ja", content="bean[{0}]を登録します")
	APPL_001,
	@Message(lang = "ja", content="bean[{0}] -> {1} を登録します")
	APPL_002,
	@Message(lang = "ja", content="action[{0}] -> {1} を登録します")
	APPL_003,
	@Message(lang = "ja", content="プラグイン[{0}]を読み込みました")
	APPL_004
	;
}
