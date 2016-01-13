package de.fh.zwickau.scriptsprachen.findme.client.location;

class Vector implements Serializable {

    def x, y

    @Override
    public String toString() {
        return x + " " + y
    }

    public static Vector fromString(String s) {
        try {
            Vector v = new Vector()
            String[] coords = s.split(" ")
            v.x = Double.parseDouble(coords[0])
            v.y = Double.parseDouble(coords[1])
            return v
        } catch (NumberFormatException ex) {
            println "Error while parsing Vector from String: " + ex
            return new Vector(x: null, y: null)
        }
    }

}