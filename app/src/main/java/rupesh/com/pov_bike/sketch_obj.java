package rupesh.com.pov_bike;

/**
 * Created by rupesh kashyap on 2/2/2017.
 */

public class sketch_obj {

    private String name = " not successful";
    private String imageId ;
    private static final int imageav = -1;



    public sketch_obj(String a, String b)
    {
        name = a ;
        imageId = b;
    }

    public void setname(String a )
    {
      name=a;
    }
    public void setsketchid(String a)
    {
        imageId=a;
    }

    public String getImageId()
    {
        return imageId;
    }
    public String getName()
    {
        return name;
    }

}
