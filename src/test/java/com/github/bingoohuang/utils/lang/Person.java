package com.github.bingoohuang.utils.lang;

public class Person {
    String name;
    String idNo;
    String creditCard;

    public Person() {
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", idNo='" + idNo + '\'' +
                ", creditCard='" + creditCard + '\'' +
                '}';
    }

    public Person(String name, String idNo, String creditCard) {
        this.name = name;
        this.idNo = idNo;
        this.creditCard = creditCard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (name != null ? !name.equals(person.name) : person.name != null) return false;
        if (idNo != null ? !idNo.equals(person.idNo) : person.idNo != null) return false;
        return !(creditCard != null ? !creditCard.equals(person.creditCard) : person.creditCard != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (idNo != null ? idNo.hashCode() : 0);
        result = 31 * result + (creditCard != null ? creditCard.hashCode() : 0);
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }
}
