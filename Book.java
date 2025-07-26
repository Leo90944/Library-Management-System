import java.io.Serializable;
import java.util.Objects;

/**
 * Encapsulates information about a book.
 * @author Balaji Srinivasan
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private int numberOfCopies;
    private int availableCopies;

    /**
     * Constructor. Most properties (except number of copies are read only)
     */
    public Book(String title, String author, String isbn, int publicationYear, int numberOfCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.numberOfCopies = numberOfCopies;
        this.availableCopies = numberOfCopies;
    }

    /**
     * @return The title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The author of the book.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the ISBN for this book.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @return The publication year of this book.
     */
    public int getPublicationYear() {
        return publicationYear;
    }

    /**
     * @return The number of copies of this book.
     */
    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    /**
     * @return The number of available copies of this book.
     */
    public int getAvailableCopies() {
        return availableCopies;
    }

    /**
     * Sets the number of available copies. Just used for testing.
     */
    public void setAvailableCopies(int numCopies) {
        if (numCopies < 0 || numCopies > numberOfCopies) {
            throw new IllegalArgumentException("Invalid number of available copies: " + numCopies +
                                                ". Must be between 0 and " + numberOfCopies + ".");
        }
        this.availableCopies = numCopies;
    }

    /**
     * Adds the given number of copies of this book to the library.
     * @throws IllegalArgumentException if numCopiesToAdd is negative.
     */
    public void addCopies(int numCopiesToAdd) {
        if (numCopiesToAdd < 0) {
            throw new IllegalArgumentException("Number of copies to add cannot be negative");
        }
        numberOfCopies += numCopiesToAdd;
        availableCopies += numCopiesToAdd;
    }

    /** 
     * Checks out a book (decrements number of copies available in the library)
     * @throws RuntimeException if no copies are available to check out.
     */
    public void checkout() {
        if (availableCopies <= 0) {
            throw new NoCopiesAvailableException("No copies available to checkout for book: '" + title + "' (ISBN: " + isbn + ")");
        }
        availableCopies--;
    } 

    /** 
     * Checks in a book into the library.
     * @throws AllCopiesAlreadyCheckedInException if no copies have been checked out.
     */
    public void checkin() {
        if (availableCopies >= numberOfCopies) {
            throw new AllCopiesAlreadyCheckedInException("All copies of book: '" + title + "' (ISBN: " + isbn + ") are already checked in.");
        }
        availableCopies++;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, isbn);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        Book other = (Book) that;
        return Objects.equals(title, other.title) && 
               Objects.equals(author, other.author) && 
               Objects.equals(isbn, other.isbn);
    }

    /**
     * Provides string representation of the Book object.
     */
    @Override
    public String toString() {
        return "Title: '" + title + '\'' +
        ", Author: '" + author + '\'' +
        ", ISBN: '" + isbn + '\'' +
        ", Year: " + publicationYear +
        ", Total Copies: " + numberOfCopies +
        ", Available Copies: " + availableCopies;
    }
}
