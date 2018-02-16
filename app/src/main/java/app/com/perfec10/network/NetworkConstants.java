package app.com.perfec10.network;

/**
 * Created by fluper on 25/10/17.
 */

public class NetworkConstants {


 // http://18.217.249.143/perfec10/api/login

  //  public static String baseUrl = "http://34.239.111.108/perfec10/api/"; // for base url
   // public static String baseUrl = "http://34.239.111.108/developer/perfec10/api/"; // for local url
    public static String baseUrl = "http://18.217.249.143/perfec10/api/"; // for local url

      // http://34.239.111.108/developer/perfec10/api/signup
  //  public static String localUrl = "http://34.239.111.108/developer/perfec10/api/";
    public static String localUrl = "http://18.217.249.143/perfec10/api/";
    //http://52.70.174.112/perfec10/groupImage/imagename
   // public static String basegroupUrl = "http://52.70.174.112/perfec10/groupImage/";

    //http://52.70.174.112/perfec10/profileImage/imagename
   // public static String imageBaseUrl = "http://52.70.174.112/perfec10/";
  //  public static String imageBaseUrl = "http://34.239.111.108/perfec10/";
   // public static String imageBaseUrl = "http://34.239.111.108/developer/perfec10/";
    public static String imageBaseUrl = "http://18.217.249.143/perfec10/";



    public static String acceptTermsAndConditions = baseUrl + "acceptTermsAndConditions";

    public static String signupUrl = baseUrl + "signup";
    public static String verifyEmailUrl = baseUrl + "verifyEmail";
    public static String resendUrl = baseUrl + "resend";
    public static String changeMailUrl = baseUrl + "changeMail";
    public static String loginUrl = baseUrl + "login";
    public static String walkThroughUrl = baseUrl + "walkThrough";
    //http://52.70.174.112/perfec10/api/userInput
    public static String userInputUrl = baseUrl + "userInput";
    public static String getUserDetail = baseUrl + "getUserDetail";
    public static String searchUser = baseUrl + "searchUser";
    public static String friendList = baseUrl + "friendList";
    public static String updatePost = baseUrl + "updatePost";
    public static String updateProfile = baseUrl + "updateProfile";
    public static String addFriend = baseUrl + "addFriend";
    public static String editGroup = baseUrl + "editGroup";
    public static String groupDetail = baseUrl + "groupDetail";
    public static String createGroup = baseUrl + "createGroup";
    public static String deleteFriend = baseUrl + "deleteFriend";
    public static String deleteGroup = baseUrl + "deleteGroup";
    public static String deleteGroupMember = baseUrl + "deleteGroupMember";
    public static String addgroupMember = baseUrl + "addgroupMember";
    public static String userPost = baseUrl + "userPost";
    public static String friendPost = baseUrl + "postShareTome";
    public static String recentShare = baseUrl + "recentShare";
    public static String postLikeUsers = baseUrl + "postLikeUsers";
    public static String postCommentUsers = baseUrl + "postCommentUsers";
    public static String commentOnPost = baseUrl + "commentOnPost";
    public static String postLike = baseUrl + "postLike";
    public static String getPost = baseUrl + "getPost";
    public static String report = baseUrl + "report";
    public static String logout = baseUrl + "logout";
    public static String linking = baseUrl + "linking";
    public static String unLink = baseUrl + "unLink";
    public static String deleteUserPost = baseUrl + "deleteUserPost";

    public static int requestCodeSignup = 1;

    public static final String TIMEOUT = "Oops timeout!!!";
}
