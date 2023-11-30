package analix.DHIT.input;

public class MemberSearchInput {

    private String searchCharacters;

    public String getSearchCharacters() {
        return searchCharacters;
    }

    public void setSearchCharacters(String searchCharacters) {
        this.searchCharacters = searchCharacters;
    }

    public MemberSearchInput withSearchCharacters(String searchCharacters){
        MemberSearchInput memberSearchInput = new MemberSearchInput();
        memberSearchInput.setSearchCharacters(searchCharacters);
        return memberSearchInput;
    }
}
