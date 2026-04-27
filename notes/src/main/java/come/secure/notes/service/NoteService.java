package come.secure.notes.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import come.secure.notes.entity.Note;
import come.secure.notes.entity.User;
import come.secure.notes.repositories.NoteRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface NoteService {
    Note createNoteForUser(String username, String content);

    Note updateNoteForUser(Long noteId, String content, String username);

    void deleteNoteForUser(Long noteId, String username);

    List<Note> getNotesForUser(String username);

    @Service
    @RequiredArgsConstructor
    class NoteServiceImpl implements NoteService {
        @Autowired
        NoteRepository noteRepository;
        @Override
        public Note createNoteForUser(String username, String content) {
            Note note = new Note();
            note.setContent(content);
            note.setOwnerUsername(username);
            return noteRepository.save(note);
        }

        @Override
        public Note updateNoteForUser(Long noteId, String content, String username) {
            Note note = noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found"));
            note.setContent(content);
            return noteRepository.save(note);
        }

        @Override
        public void deleteNoteForUser(Long noteId, String username) {
            noteRepository.deleteById(noteId);
        }

        @Override
        public List<Note> getNotesForUser(String username) {
            return noteRepository.findByOwnerUsername(username);
        }
    }

    @NoArgsConstructor
    @Data
    @Service
    class UserDetailsImpl implements UserDetails {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String username;
        private String email;

        @JsonIgnore
        private String password;

        private boolean is2faEnabled;

        private Collection<? extends GrantedAuthority> authorities;

        public UserDetailsImpl(Long id, String username, String email, String password,
                               boolean is2faEnabled, Collection<? extends GrantedAuthority> authorities) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.password = password;
            this.is2faEnabled = is2faEnabled;
            this.authorities = authorities;
        }

        public static UserDetailsImpl build(User user) {
            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName().name());

            return new UserDetailsImpl(
                    user.getUserId(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.isTwoFactorEnabled(),
                    List.of(authority) // Wrapping the single authority in a list
            );
        }


        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            UserDetailsImpl user = (UserDetailsImpl) o;
            return Objects.equals(id, user.id);
        }
    }
}
