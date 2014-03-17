package net.basilwang.enums;

public enum TAHelperDownloadPhrase {
	NoPhrase(0),
	GetLogonViewStateForPostPhrase(5),
	LogonPhrase(10),
	GetXSMainPhrase(15),
	SetScoreViewStateForPostPhrase(50),
	SetCurriculumViewStateForPostPhrase(50),
	GetCurriculumByCemesterIndexPhrase(100),
	GetScorePhrase(100);
	
	
       
    private final int value;
    public int getValue() {
        return value;
    }
    //构造器默认也只能是private, 从而保证构造函数只能在内部使用
    TAHelperDownloadPhrase(int value) {
        this.value = value;
    }
}
