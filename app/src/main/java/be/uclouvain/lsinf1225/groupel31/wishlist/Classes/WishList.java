package be.uclouvain.lsinf1225.groupel31.wishlist.Classes;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import be.uclouvain.lsinf1225.groupel31.wishlist.tools.AccessDataBase;
import be.uclouvain.lsinf1225.groupel31.wishlist.tools.ImageToBlob;

public class WishList {

    private String name;
    private Bitmap picture;
    private Integer size;
    private String owner;
    private Integer id;
    private AccessDataBase db;
    private List<Wish> wishLst = new ArrayList<>();
    private List<User> permitted = new ArrayList<>();

    /** Constructor
     * @param db AccessDataBase to insert or select in database
     * @param id wishlist'is
     * @param name wishlist name
     * @param picture wishlist's picture
     * @param size wishlist's size
     * @param owner wishlist's owner
     */
    public WishList(AccessDataBase db, Integer id, String name, Bitmap picture, Integer size, String owner){
        this.db = db;
        setId(id);
        setName(name);
        setPicture(picture);
        setSize(size);
        setOwner(owner);
        updateWishLst();
        updatePermitted();
    }

    public void updatePermitted() {
        List<User> permittedTemp = new ArrayList<>();
        Cursor cursor = db.select("SELECT * FROM Perm WHERE id=" + this.getId() + ";");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            User toAdd = new User();
            toAdd.setDb(db.getContext());
            toAdd.setLessRefFromDb(cursor.getString(cursor.getColumnIndex("mail")));
            toAdd.setPerm(cursor.getInt(1));
            permittedTemp.add(toAdd);
            cursor.moveToNext();
        }
        cursor.close();
        this.permitted = permittedTemp;
    }

    /** Create a list of wish from the db data
     */
    private void updateWishLst() {
        //Select
        String req = "SELECT W.* FROM Wishlist L, Wish W, Content C WHERE C.wishlist=\"" +
                this.getId() + "\" AND C.product = W.num GROUP BY W.num;";
        Cursor cursor = db.select(req);
        List<Wish> wishes = new ArrayList<>();
        cursor.moveToFirst();
        //loop to append wish in list
        while(!cursor.isAfterLast()){
            Wish toAdd = new Wish(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(3), cursor.getDouble(4), cursor.getString(5));
            if(cursor.getBlob(2) != null){
                toAdd.setPicture(ImageToBlob.getBytePhoto(cursor.getBlob(2)));
            }
            wishes.add(toAdd);
            cursor.moveToNext();
        }
        setWishLst(wishes);
        cursor.close();

        this.size = wishes.size();
    }

    /** insert a new line in db table wish
     * @param name new wish's name
     * @param picture new wish's picture
     * @param description new wish's description
     * @param price new wish's price
     * @param market new wish's market where you can find it
     */
    public void createWish(String name, Bitmap picture, String description,
                           double price, String market){
        // insert
        String req = "INSERT INTO Wish (name, desc, price, market) VALUES ";
        req += "(\"" + name + "\", \"" + description + "\", \""
                + price + "\", \"" + market + "\");";
        db.insert(req);

        // select to find id of last wish inserted
        Cursor cursor = db.select("SELECT * FROM Wish");
        cursor.moveToLast();
        int wish_id = cursor.getInt(0); // get id
        cursor.close();

        // update picture
        if(picture != null) {
            ContentValues values = new ContentValues();
            values.put("photo", ImageToBlob.getBytes(picture));
            String selection = "num LIKE ?";
            String[] selectionArg = {String.format("%s", wish_id)};
            db.get().update("Wish", values, selection, selectionArg);
        }

        // link wish
        this.linkWish(wish_id);
    }

    public void linkWish(int wish_id){
        //insert link wishlist-wish in db table content
        String req = "INSERT INTO Content (wishlist, product) VALUES (\"" + this.id + "\", \"" + wish_id + "\");";
        db.insert(req);
        this.size++;
        db.insert("UPDATE Wishlist SET size=" + this.size + " WHERE id=" + this.getId());
        updateWishLst();
    }

    public void changeName(String input) {
        db.insert("UPDATE Wishlist SET name=\"" + input + "\" WHERE id=" +this.getId());
        this.setName(input);
    }

    public void updatePicture(Bitmap picture){
        if(picture == null){
            this.setPicture(null);
        }else {
            ContentValues values = new ContentValues();
            values.put("picture", ImageToBlob.getBytes(picture));
            String selection = "id LIKE ?";
            String[] selectionArg = {String.format("%s", this.getId())};
            db.get().update("Wishlist", values, selection, selectionArg);
            this.setPicture(picture);
        }
    }

    public void unlinkWish(int wish_id){
        String req = "DELETE FROM Content WHERE wishlist=\""+
                this.getId() +"\" AND product=\""+wish_id +"\";";
        db.insert(req);
        this.updateWishLst();
    }

    // ***** Getters and setters *****
    public String getName() {
        return this.name;
    }

    public Bitmap getPicture() {
        return this.picture;
    }

    public Integer getSize() {
        return this.size;
    }

    public String getOwner(){
        return this.owner;
    }

    public List<Wish> getWishLst(){
        return this.wishLst;
    }

    private void setOwner(String owner){
        this.owner = owner;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private void setWishLst(List<Wish> wishLst) {
        this.wishLst = wishLst;
    }

    public List<User> getPermitted() {
        return permitted;
    }
}
