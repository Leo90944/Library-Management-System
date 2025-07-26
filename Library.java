import java.util.*;
import javax.management.RuntimeErrorException;
import java.io.*;
import java.time.Year;

/**
 * A library management class. Has a simple shell that users can interact with to add/remove/checkout/list books in the library.
 * Also allows saving the library state to a file and reloading it from the file.
 * @author Joey Lu
 */
public class Library {
    private List<Book> books;
    private Map<String, Book> isbnToBookMap;

    public Library() {
        books = new ArrayList<>();
        isbnToBookMap = new HashMap<>();
    }

    /**
     * @return the number of books (not number of copies) in the library.
     * @author Leo
     */
    public int getNumberOfBooks() {
        return books.size();
    }

    /**
     * Adds a book to the library. If the library already has this book then it
     * adds the number of copies the library has.
     * @author Leo
     */
    public void addBook(Book newBook) {
        String isbn = newBook.getIsbn();
        if (isbnToBookMap.containsKey(isbn)) {
            Book existingBook = isbnToBookMap.get(isbn);
            existingBook.addCopies(newBook.getNumberOfCopies());
        } else {
            books.add(newBook);
            isbnToBookMap.put(isbn, newBook);
        }
    }

    /**
     * Checks out a book from the library. If the book is not found or if there are no available copies, it throws an exception.
     * Big-O Analysis: O(1) on average - HashMap get and Book checkout are constant time.
     * @param isbn The ISBN of the book to check out.
     * @throws IllegalArgumentException if ISBN is null or empty, or if the book does not exist or has no available copies.
     * @throws NoCopiesAvailableException if book exists but has no available copies (from Book class).
     * @author Joey Lu
     */
    public void checkout(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }
        Book book = isbnToBookMap.get(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " not found in library.");
        }
        book.checkout();
        System.out.println("Checked out: " + book.getTitle() + " (Available copies: " + book.getAvailableCopies() + ")");
    }

    /**
     * Returns a book to the library
     * Big-O Analysis: O(1) on average - HashMap get and Book checkout are constant time.
     * @param isbn The ISBN of book to return.
     * @throws IllegalArgumentException if ISBN is null or return.
     * @throws RunTimeException if book does not exist or if all copies are already in library.
     * @throws AllCopiesAlreadyCheckedInException if all copies are already in library (from Book class).
     * @author Joey Lu
     */
    public void returnBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }
        Book book = isbnToBookMap.get(isbn); 
        if (book == null) {
            throw new RuntimeException("Book with ISBN " + isbn + " not found in the library.");
        }
        book.checkin();
        System.out.println("Returned: " + book.getTitle() + " (Available copies: " + book.getAvailableCopies() + ")");
    }

    /**
     * Finds a book by its title and author. Throws an exception if the book is not found.
     * Big-O Analysis: O(n) on average where n is the number of books in the library. 
     * @param title The title of the book to find.
     * @param author The author of the book to find.
     * @return The book if found.
     * @throws IllegalArgumentException if title or author is null or empty.
     * @throws RuntimeException if the book is not found.
     * @author Joey Lu
     */
    public Book findByTitleAndAuthor(String title, String author) {
        if (title == null || title.trim().isEmpty() || author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Title and author cannot be null or empty.");
        }
        for (Book book : booksByIsbn.values()) {
            if (book.getTitle().equalsIgnoreCase(title) && book.getAuthor().equalsIgnoreCase(author)) {
                return book;
            }
        }
        throw new RuntimeException("Book with title '" + title + "' and author '" + author + "' not found.");
    }

    /**
     * Finds a book by its ISBN. Throws an exception if the book is not found.
     * Big-O Analysis: O(1) on average - HashMap get is constant time.
     * @param isbn The ISBN of the book to find.
     * @return The book if found.
     * @throws IllegalArgumentException if ISBN is null or empty.
     * @throws RuntimeException if the book is not found.
     * @author Joey Lu
     */
    public Book findByISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }
        Book book = isbnToBookMap.get(isbn);
        if (book == null) {
            throw new RuntimeException("Book with ISBN" + isbn + " not found.");  
        }
        return book;
    }

    /**
     * Saves the contents of this library to the given file.
     * Big-O Analysis: O(n) on average where n is the number of books in the internal hashmap.
     * @param filename the name of the file to be written to
     * @throws IOException if the file cannot be opened for writing
     * @author Anthony Ngo
     */
    public void save(String filename) {
        File out = new File(filename);
        try (PrintWriter writer = new PrintWriter(out)) {
            for (Book book : books) {
                writer.printf("%s,%s,%s,%d,%d\n",
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublicationYear(),
                    book.getNumberOfCopies());
            }
        } catch (IOException ex) {
            System.err.println("Save unsuccessful. Output file unable to be opened." + filename + ". " + ex.getMessage());
            throw ex;
        }
        System.out.println("Library successfully saved to " + filename);
    }

    /**
     * Loads the contents of this library from the given file. All existing data is cleared.
     * Big-O Analysis: O(n) where n is the number of lines in the file.
     * @param filename the name of the file to be loaded
     * @author Jonathan Tran
     */
    public void load(String filename) {
        Map<String, Book> loadedBooks = new HashMap<>();
        File in = new File(filename);
        try (Scanner inScan = new Scanner(in)) {
            while (inScan.hasNextLine()) {
                String lineContent = inScan.nextLine();
                String[] lineParts = lineContent.split(",");

                if (lineParts.length != 5) {
                    System.err.println("Load unsuccessful. Malformed line: '" + lineContent + "'");
                    throw new IOException("Malformed data in file: " + filename);
                }

                String title = lineParts[0].strip();
                String author = lineParts[1].strip();
                String isbn = lineParts[2].strip();
                int publicationYear = Integer.parseInt(lineParts[3].strip());
                int numberOfCopies = Integer.parseInt(lineParts[4].strip());

                if (numberOfCopies < 0) {
                    System.err.println("Load unsuccessful. Expected nonnegative number of copies.");
                    throw new IOException("Invalid number of copies for book with ISBN: " + isbn);
                }

                if (publicationYear > Year.now().getValue()) {
                    System.err.println("Load unsuccessful. Expected year <= current year.");
                    throw new IOException("Invalid publication year for book with ISBN: " + isbn);
                }

                Book newBook = new Book(title, author, isbn, publicationYear, numberOfCopies);
                loadedBooks.put(isbn, newBook);
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeErrorException("Load unsuccessful. Cannot find file " + filename + ".", ex);
        } catch (NumberFormatException ex) {
            throw new RuntimeErrorException("Load unsuccessful. Non-integer values present.", ex);
        } catch (IOException ex) {
            throw new RuntimeErrorException("Load unsuccessful due to file content error: " + ex.getMessage(), ex);
        }

        isbnToBookMap = loadedBooks;
        books = new ArrayList<>(loadedBooks.values());
        System.out.println("Library successfully loaded from " + filename);
    }
}
