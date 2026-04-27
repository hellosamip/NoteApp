package come.secure.notes.controller;

import come.secure.notes.entity.Note;
import come.secure.notes.service.NoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping("/hello")
    public String defaultMessage() {
        return "Hello World!";
    }
    @PostMapping("/create")
    public Note createNote(@RequestBody Map<String, String> request,
                           @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        String content = request.get("content");

        return noteService.createNoteForUser(username, content);
    }

    @GetMapping("/get")
    public List<Note> getUserNotes(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        return noteService.getNotesForUser(username);
    }

    @PutMapping("/update/{noteId}")
    public Note updateNote(@PathVariable Long noteId,
                           @RequestBody Map<String, String> request,
                           @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        String content = request.get("content");

        return noteService.updateNoteForUser(noteId, content, username);
    }

    @DeleteMapping("delete/{noteId}")
    public void deleteNote(@PathVariable Long noteId,
                           @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        noteService.deleteNoteForUser(noteId, username);
    }
}